package io.shiftleft.slice.services

import io.shiftleft.codepropertygraph.Cpg
import io.shiftleft.codepropertygraph.generated.Operators
import io.shiftleft.codepropertygraph.generated.nodes.{Call, Method}
import io.shiftleft.semanticcpg.language.toNodeTypeStarters
import io.shiftleft.slice.Extensions.{FileTraversalExtensions, MethodTraversalExtensions, NodeExtensions}
import io.shiftleft.slice.models.EdgeType
import overflowdb.Node
import overflowdb.traversal.jIteratortoTraversal

import java.io.File
import scala.annotation.tailrec
import scala.collection.mutable

class ShiftLeftService(cpg: Cpg) {
  private val map: Map[String, List[Method]] = cpg.method.filterUserDefined.toList.groupBy(m => m.filename)

  def getFiles(): List[File] = {
    cpg.file
      .filterUserDefined
      .map(file => new File(file.name))
      .toList
  }

  def getMethods(targetFile: File): List[Method] = {
    if (!map.contains(targetFile.getAbsolutePath))
      return List()

    map(targetFile.getAbsolutePath)
  }
}