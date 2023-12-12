package io.shiftleft.slice.models

case class JGraph(filePath: String, label: String, methodName: String, lineNumber: Int,
                  originalCode: List[String], nodes: List[JNode], astEdges: List[JEdge], cfgEdges: List[JEdge],
                  reachingDefEdges: List[JEdge], cdgEdges: List[JEdge])
