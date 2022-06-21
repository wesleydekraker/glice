package io.shiftleft.slice.models

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class CGraph(val startNodeId: Long) {
  private val edgesByType: mutable.Map[String, ListBuffer[CEdge]] = mutable.Map()
  private val astEdges: mutable.Map[Long, ListBuffer[CEdge]] = mutable.Map()
  private val nodesById: mutable.Map[Long, CNode] = mutable.SortedMap()
  private val properties: mutable.Map[String, String] = mutable.Map()

  def appendEdge(edge: CEdge): Unit = {
    if (!edgesByType.contains(edge.edgeType))
      edgesByType(edge.edgeType) = ListBuffer()

    if (edge.edgeType == EdgeType.AST) {
      val outEdges = astEdges.getOrElseUpdate(edge.from, new ListBuffer())
      outEdges.append(edge)
    }

    edgesByType(edge.edgeType) += edge
  }

  def appendEdges(edges: Iterable[CEdge]): Unit = {
    edges.foreach(appendEdge)
  }

  def appendNode(node: CNode): Unit = {
    nodesById(node.id) = node
  }

  def appendNodes(nodes: Iterable[CNode]): Unit = {
    nodes.foreach(appendNode)
  }

  def newGraph(): CGraph = {
    val startNode = nodesById(startNodeId)
    val nodeIds = getNodeIds(startNode)

    val newIndex = nodeIds.zipWithIndex.toMap
    val newGraph = new CGraph(newIndex(startNodeId))

    for (nodeId <- nodeIds) {
      newGraph.appendNode(CNode(newIndex(nodeId), nodesById(nodeId).nodeType, nodesById(nodeId).value))
    }

    val newEdges = edgesByType
      .flatMap(edge => edge._2)
      .map(edge => CEdge(newIndex(edge.from), newIndex(edge.to), edge.edgeType))

    newGraph.appendEdges(newEdges)

    for (property <- properties)
      newGraph.setProperty(property._1, property._2)

    newGraph
  }

  private def getNodeIds(node: CNode): List[Long] = {
    if (!astEdges.contains(node.id))
      return List(node.id)

    val listBuffer = ListBuffer[Long]()

    listBuffer += node.id
    listBuffer ++= astEdges(node.id).map(edge => nodesById(edge.to)).flatMap(getNodeIds)

    listBuffer.toList
  }

  def getEdges(edgeTypes: String*): List[CEdge] = {
    edgeTypes
      .flatMap(edgeType => edgesByType.getOrElse(edgeType, new ListBuffer()))
      .sortBy(e => (e.from, e.to))
      .toList
  }

  def getEdgeTypes(): Set[String] = {
    edgesByType.keys.toSet
  }

  def getAstEdges(node: CNode): List[CNode] = {
    astEdges.getOrElse(node.id, new ListBuffer()).map(edge => nodesById(edge.to)).toList
  }

  def nodes: List[CNode] = {
    nodesById.values.toList
  }

  def setProperty(key: String, value: String): Unit = {
    properties(key) = value
  }

  def getProperty(key: String): String = {
    properties(key)
  }

  def startNode(): CNode = {
    nodesById(startNodeId)
  }

  def appendGraph(graph: CGraph): Unit = {
    val offsetNodeId = nodesById.size
    val nodes = graph.nodes.map(node => CNode(node.id + offsetNodeId, node.nodeType, node.value))
    val edges = graph.edgesByType.values.flatten
      .map(edge => CEdge(edge.from + offsetNodeId, edge.to + offsetNodeId, edge.edgeType))

    val newOriginalCode = getProperty(GraphProperty.ORIGINAL_CODE) + graph.getProperty(GraphProperty.ORIGINAL_CODE)
    setProperty(GraphProperty.ORIGINAL_CODE, newOriginalCode)

    val newGeneratedCode = getProperty(GraphProperty.GENERATED_CODE) + graph.getProperty(GraphProperty.GENERATED_CODE)
    setProperty(GraphProperty.GENERATED_CODE, newGeneratedCode)

    appendNodes(nodes)
    appendEdges(edges)
  }
}
