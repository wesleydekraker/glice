package io.shiftleft.slice.services

import io.shiftleft.slice.models.{NodeType, CGraph, CNode}

class SourceCodeGenerator(private val graph: CGraph) {
  private val logger = new LogService()

  def toCode(): String = {
    val codeWriter = new CodeWriter

    toCode(graph.startNode(), codeWriter)
    val result = codeWriter.toString()
    result
  }

  def toCode(node: CNode, codeWriter: CodeWriter, isStatement: Boolean = false): Unit = {
    if (NodeType.OPERATOR_CENTER_MAP.contains(node.nodeType)) {
      operatorCenterToCode(node, codeWriter, isStatement)
    } else if (NodeType.OPERATOR_LEFT_MAP.contains(node.nodeType)) {
      operatorLeftToCode(node, codeWriter, isStatement)
    } else if (NodeType.OPERATOR_RIGHT_MAP.contains(node.nodeType)) {
      operatorRightToCode(node, codeWriter, isStatement)
    } else {
      node.nodeType match {
        case NodeType.METHOD => methodToCode(node, codeWriter, isStatement)
        case NodeType.METHOD_RETURN => methodReturnToCode(node, codeWriter, isStatement)
        case NodeType.METHOD_PARAMETER_IN => methodParameterInToCode(node, codeWriter, isStatement)
        case NodeType.METHOD_PARAMETER_OUT => methodParameterOutToCode(node, codeWriter, isStatement)
        case NodeType.TYPE_DECL => typeDeclCode(node, codeWriter, isStatement)
        case NodeType.BLOCK => blockToCode(node, codeWriter, isStatement)
        case NodeType.LOCAL => localToCode(node, codeWriter, isStatement)
        case NodeType.TYPE => typeToCode(node, codeWriter, isStatement)
        case NodeType.IDENTIFIER => identifierToCode(node, codeWriter, isStatement)
        case NodeType.FIELD_IDENTIFIER => fieldIdentifierToCode(node, codeWriter)
        case NodeType.FUNCTION_POINTER_IDENTIFIER => functionPointerIdentifierToCode(node, codeWriter, isStatement)
        case NodeType.NUMERIC_LITERAL => numericLiteralToCode(node, codeWriter, isStatement)
        case NodeType.STRING_LITERAL => stringLiteralToCode(node, codeWriter, isStatement)
        case NodeType.METHOD_CALL => methodCallToCode(node, codeWriter, isStatement)
        case NodeType.OPERATOR_FIELD_ACCESS => operatorFieldAccessToCode(node, codeWriter, isStatement)
        case NodeType.OPERATOR_INDIRECT_FIELD_ACCESS => operatorIndirectFieldAccessToCode(node, codeWriter, isStatement)
        case NodeType.OPERATOR_INDIRECT_INDEX_ACCESS => operatorIndirectIndexAccessToCode(node, codeWriter, isStatement)
        case NodeType.OPERATOR_SIZE_OF => operatorSizeOfToCode(node, codeWriter)
        case NodeType.OPERATOR_ARRAY_INITIALIZER => operatorArrayInitializerToCode(node, codeWriter)
        case NodeType.OPERATOR_CAST => castToCode(node, codeWriter)
        case NodeType.OPERATOR_CAST_TYPE_ID => castTypeIdToCode(node, codeWriter)
        case NodeType.OPERATOR_CONDITIONAL => operatorConditionalCode(node, codeWriter)
        case NodeType.CONTROL_STRUCTURE_FOR => forToCode(node, codeWriter, isStatement)
        case NodeType.CONTROL_STRUCTURE_IF => ifToCode(node, codeWriter, isStatement)
        case NodeType.CONTROL_STRUCTURE_ELSE => elseToCode(node, codeWriter, isStatement)
        case NodeType.CONTROL_STRUCTURE_DO => doToCode(node, codeWriter, isStatement)
        case NodeType.CONTROL_STRUCTURE_WHILE => whileToCode(node, codeWriter, isStatement)
        case NodeType.CONTROL_STRUCTURE_BREAK => breakToCode(node, codeWriter, isStatement)
        case NodeType.CONTROL_STRUCTURE_CONTINUE => continueToCode(node, codeWriter, isStatement)
        case NodeType.CONTROL_STRUCTURE_SWITCH => switchToCode(node, codeWriter, isStatement)
        case NodeType.CONTROL_STRUCTURE_CASE => caseToCode(node, codeWriter, isStatement)
        case NodeType.CONTROL_STRUCTURE_DEFAULT => defaultToCode(node, codeWriter, isStatement)
        case NodeType.CONTROL_STRUCTURE_GOTO => gotoToCode(node, codeWriter, isStatement)
        case NodeType.JUMP_TARGET => jumpTargetToCode(node, codeWriter, isStatement)
        case NodeType.RETURN => returnToCode(node, codeWriter, isStatement)
        case NodeType.OPERATOR_DELETE => operatorDeleteCode(node, codeWriter, isStatement)
        case NodeType.PROBLEM_DECLARATION =>
        case NodeType.PROBLEM_STATEMENT =>
        case NodeType.PROBLEM_EXPRESSION =>
        case NodeType.OPERATOR_TYPE_ID =>
        case NodeType.ASM_DECLARATION =>
        case NodeType.ARRAY_RANGE =>
        case NodeType.MEMBER =>
        case _ => unknownToCode(node, codeWriter, isStatement)
      }
    }
  }

  def operatorCenterToCode(node: CNode, codeWriter: CodeWriter, isStatement: Boolean = false): Unit = {
    val outNodes = graph.getAstEdges(node)

    if (outNodes.length != 2) {
      logger.info("Operator center should have two AST children.")
      return
    }

    codeWriter.append(isStatement)
    toCode(outNodes.head, codeWriter)

    val operator = NodeType.OPERATOR_CENTER_MAP(node.nodeType)

    codeWriter.append(s" $operator ")
    toCode(outNodes.last, codeWriter)

    if (isStatement)
      codeWriter.append(";\n")
  }

  def operatorLeftToCode(node: CNode, codeWriter: CodeWriter, isStatement: Boolean = false): Unit = {
    val outNodes = graph.getAstEdges(node)

    if (outNodes.length != 1) {
      logger.info("Operator left should have one AST child.")
      return
    }

    val operator = NodeType.OPERATOR_LEFT_MAP(node.nodeType)

    codeWriter.append(operator, isStatement)
    toCode(outNodes.head, codeWriter)

    if (isStatement)
      codeWriter.append(";\n")
  }

  def operatorRightToCode(node: CNode, codeWriter: CodeWriter, isStatement: Boolean = false): Unit = {
    val outNodes = graph.getAstEdges(node)

    if (outNodes.length != 1) {
      logger.info("Operator right should have one AST child.")
      return
    }

    val operator = NodeType.OPERATOR_RIGHT_MAP(node.nodeType)

    toCode(outNodes.head, codeWriter, isStatement)
    codeWriter.append(operator)

    if (isStatement) {
      codeWriter.append(";\n")
    }
  }

  def methodToCode(node: CNode, codeWriter: CodeWriter, isStatement: Boolean = false): Unit = {
    val outNodes = graph.getAstEdges(node)

    val methodReturn = outNodes.find(node => node.nodeType == NodeType.METHOD_RETURN)
    val parameterIn = outNodes.filter(node => node.nodeType == NodeType.METHOD_PARAMETER_IN)
    val block = outNodes.find(node => node.nodeType == NodeType.BLOCK)
    val functionPointer = outNodes.find(node => node.nodeType == NodeType.FUNCTION_POINTER_IDENTIFIER)

    if (methodReturn.isEmpty) {
      logger.info("Method without method return found.")
      return
    }

    toCode(methodReturn.get, codeWriter, isStatement = true)
    val methodName = if (node.value.nonEmpty) node.value else "method"
    codeWriter.append(s" $methodName(")
    parameterIn.foreach(node => toCode(node, codeWriter))
    codeWriter.append(")")

    if (functionPointer.isDefined) {
      codeWriter.append(" = ")
      toCode(functionPointer.get, codeWriter)
      codeWriter.append(";\n")
    } else if (block.isDefined) {
      codeWriter.append("\n")
      toCode(block.get, codeWriter, isStatement = true)
    } else {
      codeWriter.append(";\n")
    }
  }

  def methodReturnToCode(node: CNode, codeWriter: CodeWriter, isStatement: Boolean = false): Unit = {
    if (node.value.isEmpty) {
      logger.info("Method return is empty.")
      return
    }

    codeWriter.append(node.value, isStatement)
  }

  def methodParameterInToCode(node: CNode, codeWriter: CodeWriter, isStatement: Boolean = false): Unit = {
    val outNodes = graph.getAstEdges(node)

    if (outNodes.length == 1) {
      toCode(outNodes.head, codeWriter)
    } else if (outNodes.length == 2) {
      toCode(outNodes.head, codeWriter)
      codeWriter.append(" ")
      toCode(outNodes.last, codeWriter)
    } else {
      logger.info("Method parameter should have one or two AST children.")
    }
  }

  def methodParameterOutToCode(node: CNode, codeWriter: CodeWriter, isStatement: Boolean = false): Unit = {
    logger.info("Parameter out can not be converted to source code.")
  }

  def blockToCode(node: CNode, codeWriter: CodeWriter, isStatement: Boolean = false): Unit = {
    val outNodes = graph.getAstEdges(node)

    codeWriter.append("{", isStatement)
    if (isStatement)
      codeWriter.append("\n")
    codeWriter.addIndent()

    outNodes.foreach(node => toCode(node, codeWriter, isStatement))

    codeWriter.removeIndent()
    codeWriter.append("}", isStatement)
    if (isStatement)
      codeWriter.append("\n")
  }

  def localToCode(node: CNode, codeWriter: CodeWriter, isStatement: Boolean = false): Unit = {
    val outNodes = graph.getAstEdges(node)

    if (outNodes.length != 2) {
      logger.info("Local should have two AST children.")
      return
    }

    toCode(outNodes.head, codeWriter, isStatement)
    codeWriter.append(" ")
    toCode(outNodes.last, codeWriter)

    if (isStatement)
      codeWriter.append(";\n")
  }

  def typeToCode(node: CNode, codeWriter: CodeWriter, isStatement: Boolean = false): Unit = {
    val outNodes = graph.getAstEdges(node)

    val pointerTypes = outNodes.count(_.nodeType == NodeType.POINTER_TYPE)
    val literal = outNodes.find(_.nodeType == NodeType.NUMERIC_LITERAL)

    var code = node.value

    if (literal.isDefined)
      code += s"[${literal.get.value}]"
    code += "*" * pointerTypes

    codeWriter.append(code, isStatement)
  }

  def identifierToCode(node: CNode, codeWriter: CodeWriter, isStatement: Boolean = false): Unit = {
    codeWriter.append(node.value, isStatement)
  }

  def fieldIdentifierToCode(node: CNode, codeWriter: CodeWriter): Unit = {
    codeWriter.append(node.value)
  }

  def functionPointerIdentifierToCode(node: CNode, codeWriter: CodeWriter, isStatement: Boolean = false): Unit = {
    codeWriter.append(node.value, isStatement)
  }

  def numericLiteralToCode(node: CNode, codeWriter: CodeWriter, isStatement: Boolean = false): Unit = {
    if (node.value.isEmpty) {
      logger.info("Numeric literal is empty.")
    } else {
      codeWriter.append(node.value, isStatement)
    }

    if (isStatement)
      codeWriter.append(";\n")
  }

  def stringLiteralToCode(node: CNode, codeWriter: CodeWriter, isStatement: Boolean = false): Unit = {
    if (node.value.nonEmpty) {
      codeWriter.append(s""""${node.value}"""", isStatement)
    } else {
      val outNodes = graph.getAstEdges(node)
      val formatSpecifiers = outNodes.filter(_.nodeType == NodeType.STRING_FORMAT_SPECIFIER).map(_.value).mkString
      val nullCharacter = if (outNodes.exists(_.nodeType == NodeType.STRING_NULL_CHARACTER)) "\\0" else ""
      val stringLength = outNodes.find(_.nodeType == NodeType.STRING_LENGTH).get.value

      codeWriter.append(s""""$formatSpecifiers$nullCharacter"[$stringLength]""", isStatement)
    }

    if (isStatement)
      codeWriter.append(";\n")
  }

  def methodCallToCode(node: CNode, codeWriter: CodeWriter, isStatement: Boolean = false): Unit = {
    val outNodes = graph.getAstEdges(node)

    if (node.value.isEmpty) {
      logger.info("Method call name is empty.")
    } else {
      codeWriter.append(node.value, isStatement)
    }

    codeWriter.append("(")
    outNodes.foreach(node => {
      toCode(node, codeWriter)
      if (node != outNodes.last)
        codeWriter.append(", ")
    })
    codeWriter.append(")")

    if (isStatement)
      codeWriter.append(";\n")
  }

  def operatorFieldAccessToCode(node: CNode, codeWriter: CodeWriter, isStatement: Boolean = false): Unit = {
    val outNodes = graph.getAstEdges(node)

    if (outNodes.length != 2) {
      logger.info("Operator field access should have two AST children.")
      return
    }

    toCode(outNodes.head, codeWriter, isStatement)
    codeWriter.append(".")
    toCode(outNodes.last, codeWriter)
  }

  def operatorIndirectFieldAccessToCode(node: CNode, codeWriter: CodeWriter, isStatement: Boolean = false): Unit = {
    val outNodes = graph.getAstEdges(node)

    if (outNodes.length != 2) {
      logger.info("Operator indirect field access should have two AST children.")
      return
    }

    toCode(outNodes.head, codeWriter, isStatement)
    codeWriter.append("->")
    toCode(outNodes.last, codeWriter)
  }

  def operatorIndirectIndexAccessToCode(node: CNode, codeWriter: CodeWriter, isStatement: Boolean = false): Unit = {
    val outNodes = graph.getAstEdges(node)

    if (outNodes.length != 2) {
      logger.info("Operator indirect index access should have two AST children.")
      return
    }

    toCode(outNodes.head, codeWriter, isStatement)
    codeWriter.append("[")
    toCode(outNodes.last, codeWriter)
    codeWriter.append("]")
  }

  def operatorSizeOfToCode(node: CNode, codeWriter: CodeWriter): Unit = {
    val outNodes = graph.getAstEdges(node)

    if (outNodes.length != 1) {
      logger.info("Operator size should have one AST child.")
      return
    }

    codeWriter.append("sizeof(")
    toCode(outNodes.head, codeWriter)
    codeWriter.append(")")
  }

  def operatorArrayInitializerToCode(node: CNode, codeWriter: CodeWriter): Unit = {
    val outNodes = graph.getAstEdges(node)

    codeWriter.append("{")
    for (node <- outNodes) {
      toCode(node, codeWriter)

      if (node != outNodes.last)
        codeWriter.append(", ")
    }

    codeWriter.append("}")
  }

  def castToCode(node: CNode, codeWriter: CodeWriter): Unit = {
    val outNodes = graph.getAstEdges(node)

    if (outNodes.length != 2) {
      logger.info("Cast should have two AST children.")
      return
    }

    codeWriter.append("(")
    toCode(outNodes.head, codeWriter)
    codeWriter.append(") ")
    toCode(outNodes.last, codeWriter)
  }

  def castTypeIdToCode(node: CNode, codeWriter: CodeWriter): Unit = {
    val outNodes = graph.getAstEdges(node)

    if (outNodes.length == 1) {
      toCode(outNodes.head, codeWriter)
    } else {
      logger.info("Cast type id should have one AST child.")
    }
  }

  def forToCode(node: CNode, codeWriter: CodeWriter, isStatement: Boolean = false): Unit = {
    val outNodes = graph.getAstEdges(node)

    var initialization: Option[CNode] = None
    var condition: Option[CNode] = None
    var update: Option[CNode] = None
    var body: Option[CNode] = None

    if (outNodes.length >= 4) {
      initialization = Some(outNodes.head)
      condition = Some(outNodes(1))
      update = Some(outNodes(2))
      body = Some(outNodes(3))
    } else if (outNodes.length == 3) {
      condition = Some(outNodes.head)
      update = Some(outNodes(1))
      body = Some(outNodes(2))
    } else if (outNodes.length == 2) {
      update = Some(outNodes.head)
      body = Some(outNodes(1))
    } else if (outNodes.length == 1) {
      body = Some(outNodes.head)
    } else {
      logger.info("For loop should have at least one AST child.")
      return
    }

    codeWriter.append("for (", isStatement)
    if (initialization.nonEmpty)
      toCode(initialization.get, codeWriter)
    codeWriter.append("; ")
    if (condition.nonEmpty)
      toCode(condition.get, codeWriter)
    codeWriter.append("; ")
    if (update.nonEmpty)
      toCode(update.get, codeWriter)
    codeWriter.append(")\n")

    if (body.nonEmpty)
      toCode(body.get, codeWriter, isStatement = true)
  }

  def ifToCode(node: CNode, codeWriter: CodeWriter, isStatement: Boolean = false): Unit = {
    val outNodes = graph.getAstEdges(node)

    if (outNodes.length < 2 || outNodes.length > 3) {
      logger.info("If should have two or three AST children.")
      return
    }

    codeWriter.append("if (", isStatement)
    toCode(outNodes.head, codeWriter)
    codeWriter.append(")\n")

    outNodes.tail.foreach(node => {
      val addIndent = !Set(NodeType.BLOCK, NodeType.CONTROL_STRUCTURE_ELSE).contains(node.nodeType)

      if (addIndent)
        codeWriter.addIndent()

      toCode(node, codeWriter, isStatement = true)

      if (addIndent)
        codeWriter.removeIndent()
    })
  }

  def elseToCode(node: CNode, codeWriter: CodeWriter, isStatement: Boolean = false): Unit = {
    val outNodes = graph.getAstEdges(node)

    codeWriter.append("else", isStatement)

    if (outNodes.length != 1) {
      logger.info("Else should have one AST child.")
      return
    }

    if (outNodes.head.nodeType == NodeType.CONTROL_STRUCTURE_IF) {
      codeWriter.append(" ")
      toCode(outNodes.head, codeWriter)
    } else {
      codeWriter.append("\n")
      toCode(outNodes.head, codeWriter, isStatement = true)
    }
  }

  def doToCode(node: CNode, codeWriter: CodeWriter, isStatement: Boolean = false): Unit = {
    val outNodes = graph.getAstEdges(node)

    if (outNodes.length != 2) {
      logger.info("Do while should have two AST children.")
      return
    }

    codeWriter.append("do\n", isStatement)
    toCode(outNodes.last, codeWriter, isStatement = true)
    codeWriter.append("while (", isStatement)
    toCode(outNodes.head, codeWriter)
    codeWriter.append(");\n")
  }

  def whileToCode(node: CNode, codeWriter: CodeWriter, isStatement: Boolean = false): Unit = {
    val outNodes = graph.getAstEdges(node)

    codeWriter.append("while (", isStatement)

    if (outNodes.length == 1) {
      toCode(outNodes.head, codeWriter)
      codeWriter.append(")")
      if (isStatement)
        codeWriter.append(";\n")
    } else if (outNodes.length == 2) {
      toCode(outNodes.head, codeWriter)
      codeWriter.append(")\n")
      toCode(outNodes.last, codeWriter, isStatement = true)
    } else {
      logger.info("While should have one or two AST children.")
    }
  }

  def breakToCode(node: CNode, codeWriter: CodeWriter, isStatement: Boolean = false): Unit = {
    codeWriter.append("break;\n", isStatement)
  }

  def continueToCode(node: CNode, codeWriter: CodeWriter, isStatement: Boolean = false): Unit = {
    codeWriter.append("continue;\n", isStatement)
  }

  def switchToCode(node: CNode, codeWriter: CodeWriter, isStatement: Boolean = false): Unit = {
    val outNodes = graph.getAstEdges(node)

    if (outNodes.length != 2) {
      logger.info("Switch should have two AST children.")
      return
    }

    codeWriter.append("switch(", isStatement )
    toCode(outNodes.head, codeWriter)
    codeWriter.append(")\n")
    toCode(outNodes.last, codeWriter, isStatement = true)
  }

  def caseToCode(node: CNode, codeWriter: CodeWriter, isStatement: Boolean = false): Unit = {
    codeWriter.append("case:\n", isStatement)
  }

  def defaultToCode(node: CNode, codeWriter: CodeWriter, isStatement: Boolean = false): Unit = {
    codeWriter.append("default:\n", isStatement)
  }

  def gotoToCode(node: CNode, codeWriter: CodeWriter, isStatement: Boolean = false): Unit = {
    val outNodes = graph.getAstEdges(node)

    if (outNodes.length != 1) {
      logger.info("Go to should have one AST child.")
      return
    }

    codeWriter.append("goto ", isStatement)
    toCode(outNodes.head, codeWriter)
    if (isStatement)
      codeWriter.append(";\n")
  }

  def jumpTargetToCode(node: CNode, codeWriter: CodeWriter, isStatement: Boolean = false): Unit = {
    val outNodes = graph.getAstEdges(node)

    if (outNodes.length != 1) {
      logger.info("Jump target should have one AST child.")
      return
    }

    toCode(outNodes.head, codeWriter, isStatement)
    codeWriter.append(":")
    if (isStatement)
      codeWriter.append("\n")
  }

  def returnToCode(node: CNode, codeWriter: CodeWriter, isStatement: Boolean = false): Unit = {
    val outNodes = graph.getAstEdges(node)

    codeWriter.append("return", isStatement)

    if (outNodes.nonEmpty) {
      codeWriter.append(" ")
      toCode(outNodes.head, codeWriter)
    }

    if (isStatement)
      codeWriter.append(";\n")
  }

  def operatorConditionalCode(node: CNode, codeWriter: CodeWriter, isStatement: Boolean = false): Unit = {
    val outNodes = graph.getAstEdges(node)

    if (outNodes.length < 2 || outNodes.length > 3) {
      logger.info("Operator conditional should have two or three AST children.")
      return
    }

    toCode(outNodes.head, codeWriter)
    codeWriter.append(" ? ")
    toCode(outNodes(1), codeWriter)
    codeWriter.append(" : ")
    if (outNodes.length == 3)
      toCode(outNodes(2), codeWriter)

    if (isStatement)
      codeWriter.append(";\n")
  }

  def typeDeclCode(node: CNode, codeWriter: CodeWriter, isStatement: Boolean = false): Unit = {
    val outNodes = graph.getAstEdges(node)

    codeWriter.append(node.value, isStatement)

    outNodes.foreach(node => toCode(node, codeWriter))

    if (isStatement)
      codeWriter.append(";\n")
  }

  def operatorDeleteCode(node: CNode, codeWriter: CodeWriter, isStatement: Boolean = false): Unit = {
    val outNodes = graph.getAstEdges(node)

    codeWriter.append("delete ", isStatement)
    outNodes.foreach(node => toCode(node, codeWriter))

    if (isStatement)
      codeWriter.append(";\n")
  }

  def unknownToCode(node: CNode, codeWriter: CodeWriter, isStatement: Boolean = false): Unit = {
    codeWriter.append(node.nodeType)

    logger.info("Unknown type found.")
  }
}
