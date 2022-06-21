package io.shiftleft.slice

import io.shiftleft.codepropertygraph.generated.nodes.{File, Method}
import io.shiftleft.slice.models.EdgeType
import overflowdb.Node
import overflowdb.traversal.{Traversal, jIteratortoTraversal}

object Extensions {
  val usrFolder = "/usr/"
  val unknownFile = "<unknown>"
  val emptyFile = "<empty>"
  val includesFile = "<includes>"
  val globalMethod = "<global>"

  implicit class NodeExtensions(val node: Node) {
    def astChildren: List[Node] = {
      node.out(EdgeType.AST).flatMap(n => n :: n.astChildren).toList
    }
  }

  implicit class FileTraversalExtensions(val files: Traversal[File]) {
    def filterUserDefined: Traversal[File] = {
      files.filterNot(file => file.name.startsWith(usrFolder))
        .filterNot(file => file.name == unknownFile)
        .filterNot(file => file.name.endsWith(includesFile))
    }
  }

  implicit class MethodTraversalExtensions(val methods: Traversal[Method]) {
    def filterUserDefined: Traversal[Method] = {
      methods.filterNot(method => method.filename.startsWith(usrFolder))
        .filterNot(method => method.filename == unknownFile)
        .filterNot(method => method.filename == emptyFile)
        .filterNot(method => method.name == globalMethod)
    }
  }
}
