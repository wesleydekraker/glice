package io.shiftleft.slice.services

import io.shiftleft.codepropertygraph.Cpg
import io.shiftleft.codepropertygraph.generated.nodes.Method
import io.shiftleft.codepropertygraph.generated.{NodeTypes, Operators, Properties}
import io.shiftleft.slice.Extensions.NodeExtensions
import io.shiftleft.slice.models._
import overflowdb.Node
import overflowdb.traversal.jIteratortoTraversal

import java.io.{File, FileNotFoundException}
import java.nio.charset.MalformedInputException
import java.security.MessageDigest
import scala.io.Source

class GraphBuilder(val cpg: Cpg) {
  private val messageDigest: MessageDigest = MessageDigest.getInstance("SHA-256")
  private val arrayTypePattern = "(.*)\\[(.*)\\]".r
  private val formatSpecifierCharacter = "%(.)".r
  private val nullCharacter = "\\0"

  def build(method: Method, originalFile: Option[File] = Option.empty): Option[CGraph] = {
    val astNodes = method.get :: method.get.astChildren

    val block = astNodes.find(node => node.label() == NodeTypes.BLOCK)

    if (block.isEmpty || block.get.astChildren.isEmpty)
      return None

    val graph = new CGraph(method.id())
    val edges = getEdges(astNodes, Set(EdgeType.AST, EdgeType.CFG, EdgeType.CDG, EdgeType.REACHING_DEF))

    val context = new BuildContext(graph)

    for (node <- astNodes) {
      appendNode(node, context)
    }

    graph.appendEdges(edges)

    val newGraph = graph.newGraph()

    if (originalFile.nonEmpty)
      setFilePath(originalFile.get, newGraph)

    setMethodName(method, newGraph)
    setLineNumber(method, newGraph)
    setLabel(method, newGraph)
    setCode(method, newGraph)
    setGeneratedCode(newGraph)

    Some(newGraph)
  }

  private def setFilePath(file: File, graph: CGraph): Unit = {
    graph.setProperty(GraphProperty.FILE_PATH, file.getAbsolutePath)
  }

  private def setMethodName(method: Method, graph: CGraph): Unit = {
    graph.setProperty(GraphProperty.METHOD_NAME, method.name)
  }

  private def setLineNumber(method: Method, graph: CGraph): Unit = {
    graph.setProperty(GraphProperty.LINE_NUMBER, method.lineNumber.get.toString)
  }

  private def setLabel(method: Method, graph: CGraph): Unit = {
    val methodNameLower = method.name.toLowerCase
    var labelName = GraphLabel.UNKNOWN

    val isGoodFile = method.filename.contains(f"-${GraphLabel.GOOD}.")
    val isBadFile = method.filename.contains(f"-${GraphLabel.BAD}.")

    val isGoodMethod = methodNameLower.contains(GraphLabel.GOOD)
    val isBadMethod = methodNameLower.contains(GraphLabel.BAD)

    if (isGoodFile && !isBadFile) {
      labelName = GraphLabel.GOOD
    } else if (isBadFile && !isGoodFile) {
      labelName = GraphLabel.BAD
    } else if (isGoodMethod && !isBadMethod) {
      labelName = GraphLabel.GOOD
    } else if (isBadMethod && !isGoodMethod) {
      labelName = GraphLabel.BAD
    }

    graph.setProperty(GraphProperty.LABEL, labelName)
  }

  private def setCode(method: Method, graph: CGraph): Unit = {
    var code = ""

    try {
      val source = Source.fromFile(method.filename)

      if (method.lineNumber.nonEmpty && method.lineNumberEnd.nonEmpty) {
        code = source.getLines()
          .slice(method.lineNumber.get - 1, method.lineNumberEnd.get)
          .mkString("\n")
      }

      source.close()
    } catch {
      case _: FileNotFoundException =>
      case _: MalformedInputException =>
    }

    graph.setProperty(GraphProperty.ORIGINAL_CODE, s"$code\n")

    val hash = createSha256Hash(new File(method.filename).getPath, code)
    graph.setProperty(GraphProperty.HASH, hash)
  }

  private def setGeneratedCode(graph: CGraph): Unit = {
    graph.setProperty(GraphProperty.GENERATED_CODE, new SourceCodeGenerator(graph).toCode())
  }

  private def createSha256Hash(inputStrings: String*): String = {
    inputStrings.foreach(inputString => this.messageDigest.update(inputString.getBytes()))
    val hashBytes = this.messageDigest.digest()
    hashBytes.map(hashByte => String.format("%02X", hashByte)).mkString
  }

  private def getEdges(astNodes: List[Node], edgeTypes: Set[String]): List[CEdge] = {
    val astNodeIds = astNodes.map(node => node.id()).toSet

    astNodes.flatMap(node => node.outE())
      .filter(edge => edgeTypes.contains(edge.label()))
      .filter(edge => astNodeIds.contains(edge.outNode().id()) && astNodeIds.contains(edge.inNode().id()))
      .map(edge => CEdge(edge.outNode().id, edge.inNode().id, edge.label()))
  }

  private def appendNode(node: Node, context: BuildContext): Unit = {
    val graph = context.graph

    node.label match {
      case NodeTypes.METHOD => method(node, context)
      case NodeTypes.BLOCK => block(node, context)
      case NodeTypes.CALL => call(node, context)
      case NodeTypes.IDENTIFIER => identifier(node, context)
      case NodeTypes.METHOD_PARAMETER_IN => methodParameterIn(node, context)
      case NodeTypes.METHOD_PARAMETER_OUT => methodParameterOut(node, context)
      case NodeTypes.CONTROL_STRUCTURE => controlStructure(node, context)
      case NodeTypes.LOCAL => local(node, context)
      case NodeTypes.UNKNOWN => unknown(node, context)
      case NodeTypes.METHOD_RETURN => methodReturn(node, context)
      case NodeTypes.LITERAL => literal(node, context)
      case NodeTypes.RETURN => returnNode(node, context)
      case NodeTypes.JUMP_TARGET => jumpTarget(node, context)
      case NodeTypes.FIELD_IDENTIFIER => fieldIdentifier(node, context)
      case NodeTypes.TYPE_DECL => typeDecl(node, context)
      case NodeTypes.MEMBER => member(node, context)
      case _ => graph.appendNode(CNode(node.id, NodeType.UNKNOWN))
    }
  }

  private def method(node: Node, context: BuildContext): Unit = {
    val graph = context.graph
    val fullName = node.property(Properties.FULL_NAME, "")
    val name = node.property(Properties.NAME, "")

    val isInnerFunction = fullName.contains(".")

    if (isInnerFunction) {
      val fileName = node.property(Properties.FILENAME, "")
      val lineNumber = node.property(Properties.LINE_NUMBER, Integer.valueOf(1))
      val columnNumberStart = node.property(Properties.COLUMN_NUMBER, Integer.valueOf(0))
      val columnNumberEnd = node.property(Properties.COLUMN_NUMBER_END, Integer.valueOf(0))

      var part = ""

      try {
        val bufferedSource = Source.fromFile(new File(fileName))

        part = bufferedSource.getLines()
          .toList(lineNumber - 1)
          .slice(columnNumberStart, columnNumberEnd)
          .replaceAll("\\s", "")

        bufferedSource.close
      } catch {
        case _: FileNotFoundException =>
        case _: MalformedInputException =>
      }

      if (part.contains("=")) {
        val index = part.indexOf("=")
        val identifierId = context.getNewId

        graph.appendNode(CNode(node.id, NodeType.METHOD, name))
        graph.appendNode(CNode(identifierId, NodeType.FUNCTION_POINTER_IDENTIFIER, part.substring(index + 1)))

        graph.appendEdge(CEdge(node.id, identifierId, EdgeType.AST))

        return
      }
    }

    val methodName = if (isInnerFunction) name else ""
    graph.appendNode(CNode(node.id, NodeType.METHOD, methodName))
  }

  private def block(node: Node, context: BuildContext): Unit = {
    val graph = context.graph
    graph.appendNode(CNode(node.id, NodeType.BLOCK))
  }

  private def call(node: Node, context: BuildContext): Unit = {
    val graph = context.graph
    val name = node.property(Properties.NAME, "")

    if (Operators.ALL.contains(name) || name == "<operator>.arrayInitializer") {
      val nodeType = NodeType.OPERATOR_MAP(name)
      graph.appendNode(CNode(node.id, nodeType))
    } else {
      graph.appendNode(CNode(node.id, NodeType.METHOD_CALL, name))
    }
  }

  private def identifier(node: Node, context: BuildContext): Unit = {
    val graph = context.graph
    val name = node.property(Properties.NAME, "")
    val code = node.property(Properties.CODE, "")

    val variableName = if (name.contains("Typedef")) code else context.renameVariable(code)
    graph.appendNode(CNode(node.id, NodeType.IDENTIFIER, variableName))
  }

  private def methodParameterIn(node: Node, context: BuildContext): Unit = {
    val graph = context.graph
    val name = node.property(Properties.NAME, "")
    val typeFullName = node.property(Properties.TYPE_FULL_NAME, "")

    graph.appendNode(CNode(node.id, NodeType.METHOD_PARAMETER_IN))
    typeNode(node, typeFullName, context)

    if (name.nonEmpty) {
      val variableNameId = context.getNewId
      val variableName = context.renameVariable(name, isDefinition = true)
      graph.appendNode(CNode(variableNameId, NodeType.IDENTIFIER, variableName))
      graph.appendEdge(CEdge(node.id, variableNameId, EdgeType.AST))
    }
  }

  private def methodParameterOut(node: Node, context: BuildContext): Unit = {
    val graph = context.graph
    val name = node.property(Properties.NAME, "")
    val typeFullName = node.property(Properties.TYPE_FULL_NAME, "")

    typeNode(node, typeFullName, context)

    graph.appendNode(CNode(node.id, NodeType.METHOD_PARAMETER_OUT))

    if (name.nonEmpty) {
      val variableNameId = context.getNewId

      val variableName = context.renameVariable(name, isDefinition = true)
      graph.appendNode(CNode(variableNameId, NodeType.IDENTIFIER, variableName))
      graph.appendEdge(CEdge(node.id, variableNameId, EdgeType.AST))
    }
  }

  private def controlStructure(node: Node, context: BuildContext): Unit = {
    val graph = context.graph
    val code = node.property(Properties.CODE, "")
    val controlStructureType = node.property(Properties.CONTROL_STRUCTURE_TYPE, "")

    val nodeType = NodeType.CONTROL_STRUCTURE_MAP(controlStructureType)

    graph.appendNode(CNode(node.id, nodeType))

    if (nodeType == NodeType.CONTROL_STRUCTURE_GOTO) {
      val identifierId = context.getNewId
      val identifier = code.replace(";", "").split("\\s+").last

      graph.appendNode(CNode(identifierId, NodeType.IDENTIFIER, identifier))
      graph.appendEdge(CEdge(node.id, identifierId, EdgeType.AST))
    }
  }

  private def local(node: Node, context: BuildContext): Unit = {
    val graph = context.graph
    val name = node.property(Properties.NAME, "")
    val typeFullName = node.property(Properties.TYPE_FULL_NAME, "")

    graph.appendNode(CNode(node.id, NodeType.LOCAL))
    typeNode(node, typeFullName, context)

    val variableNameId = context.getNewId
    val variableName = context.renameVariable(name, isDefinition = true)

    graph.appendNode(CNode(variableNameId, NodeType.IDENTIFIER, variableName))
    graph.appendEdge(CEdge(node.id, variableNameId, EdgeType.AST))
  }

  private def typeNode(node: Node, typeFullName: String, context: BuildContext): Unit = {
    val typeId = context.getNewId
    var typeName = typeFullName

    val graph = context.graph

    if (arrayTypePattern.matches(typeFullName)) {
      val arrayType = arrayTypePattern.findFirstMatchIn(typeFullName).get

      typeName = arrayType.group(1)
      val numericLiteral = arrayType.group(2)
      val numericLiteralId = context.getNewId

      graph.appendNode(CNode(numericLiteralId, NodeType.NUMERIC_LITERAL, numericLiteral))
      graph.appendEdge(CEdge(typeId, numericLiteralId, EdgeType.AST))
    }

    while (typeName.contains("*")) {
      typeName = typeName.replaceFirst("\\*", "")

      val pointerTypeId = context.getNewId

      graph.appendNode(CNode(pointerTypeId, NodeType.POINTER_TYPE))
      graph.appendEdge(CEdge(typeId, pointerTypeId, EdgeType.AST))
    }

    graph.appendNode(CNode(typeId, NodeType.TYPE, typeName.strip()))
    graph.appendEdge(CEdge(node.id, typeId, EdgeType.AST))
  }

  private def unknown(node: Node, context: BuildContext): Unit = {
    val graph = context.graph
    val code = node.property(Properties.CODE, "")
    val parserTypeName = node.property(Properties.PARSER_TYPE_NAME, "")

    if (parserTypeName == "CASTTypeId") {
      graph.appendNode(CNode(node.id, NodeType.OPERATOR_CAST_TYPE_ID))
      typeNode(node, code, context)
    } else if (parserTypeName == "CASTProblemDeclaration") {
      graph.appendNode(CNode(node.id, NodeType.PROBLEM_DECLARATION))
    } else if (parserTypeName == "CASTProblemStatement") {
      graph.appendNode(CNode(node.id, NodeType.PROBLEM_STATEMENT))
    } else if (parserTypeName == "CASTProblemExpression") {
      graph.appendNode(CNode(node.id, NodeType.PROBLEM_EXPRESSION))
    } else if (parserTypeName == "CPPASTTypeId") {
      graph.appendNode(CNode(node.id, NodeType.OPERATOR_TYPE_ID))
    } else if (parserTypeName == "CASTASMDeclaration") {
      graph.appendNode(CNode(node.id, NodeType.ASM_DECLARATION))
    } else if (parserTypeName == "CASTArrayRangeDesignator") {
      graph.appendNode(CNode(node.id, NodeType.ARRAY_RANGE))
    } else {
      graph.appendNode(CNode(node.id, NodeType.UNKNOWN))
    }
  }

  private def methodReturn(node: Node, context: BuildContext): Unit = {
    val graph = context.graph
    val code = node.property(Properties.CODE, "")
    graph.appendNode(CNode(node.id(), NodeType.METHOD_RETURN, code))
  }

  private def literal(node: Node, context: BuildContext): Unit = {
    val graph = context.graph
    val code = node.property(Properties.CODE, "")

    if (code.contains("\"")) {
      val firstChar = code.indexOf("\"")
      val stringLiteral = code.slice(firstChar + 1, code.length - 1)
      graph.appendNode(CNode(node.id(), NodeType.STRING_LITERAL))

      val formatSpecifiers = formatSpecifierCharacter.findAllIn(stringLiteral)

      for (formatSpecifier <- formatSpecifiers) {
        val formatSpecifierId = context.getNewId

        graph.appendNode(CNode(formatSpecifierId, NodeType.STRING_FORMAT_SPECIFIER, formatSpecifier))
        graph.appendEdge(CEdge(node.id(), formatSpecifierId, EdgeType.AST))
      }

      if (stringLiteral.contains(nullCharacter)) {
        val nullCharacterId = context.getNewId

        graph.appendNode(CNode(nullCharacterId, NodeType.STRING_NULL_CHARACTER))
        graph.appendEdge(CEdge(node.id(), nullCharacterId, EdgeType.AST))
      }

      val stringLengthId = context.getNewId
      graph.appendNode(CNode(stringLengthId, NodeType.STRING_LENGTH, stringLiteral.length.toString))
      graph.appendEdge(CEdge(node.id(), stringLengthId, EdgeType.AST))

    } else if (code.contains("\'")) {
      val firstChar = code.indexOf("\'")
      val stringLiteral = code.slice(firstChar + 1, code.length - 1)
      graph.appendNode(CNode(node.id(), NodeType.STRING_LITERAL, stringLiteral))
    } else {
      graph.appendNode(CNode(node.id(), NodeType.NUMERIC_LITERAL, code))
    }
  }

  private def returnNode(node: Node, context: BuildContext): Unit = {
    val graph = context.graph
    graph.appendNode(CNode(node.id(), NodeType.RETURN))
  }

  private def jumpTarget(node: Node, context: BuildContext): Unit = {
    val graph = context.graph
    val name = node.property(Properties.NAME, "")

    if (name == "case") {
      graph.appendNode(CNode(node.id, NodeType.CONTROL_STRUCTURE_CASE))
    } else if (name == "default") {
      graph.appendNode(CNode(node.id, NodeType.CONTROL_STRUCTURE_DEFAULT))
    } else {
      val childId = context.getNewId

      graph.appendNode(CNode(node.id, NodeType.JUMP_TARGET))

      graph.appendNode(CNode(childId, NodeType.IDENTIFIER, name))
      graph.appendEdge(CEdge(node.id, childId, EdgeType.AST))
    }
  }

  private def fieldIdentifier(node: Node, context: BuildContext): Unit = {
    val graph = context.graph
    val code = node.property(Properties.CODE, "")
    graph.appendNode(CNode(node.id(), NodeType.FIELD_IDENTIFIER, code))
  }

  private def typeDecl(node: Node, context: BuildContext): Unit = {
    val graph = context.graph
    val name = node.property(Properties.NAME, "")
    graph.appendNode(CNode(node.id(), NodeType.TYPE_DECL, name))
  }

  private def member(node: Node, context: BuildContext): Unit = {
    val graph = context.graph
    graph.appendNode(CNode(node.id, NodeType.MEMBER))
  }
}
