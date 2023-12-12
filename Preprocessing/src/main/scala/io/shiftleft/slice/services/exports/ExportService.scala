package io.shiftleft.slice.services.exports

import io.shiftleft.slice.models._
import net.liftweb.json.Serialization.writePretty
import net.liftweb.json._

import java.io._
import java.nio.file.Paths

class ExportService(private val outFolder: String) {
    private implicit val formats: DefaultFormats.type = DefaultFormats

    def writeGraph(graph: CGraph): Unit = {
        writeGraph(graph, writeToFile)
    }

    def writeGraph(graph: CGraph, writeToFile: (File, String) => Unit): Unit = {
        val filePath = graph.getProperty(GraphProperty.FILE_PATH)
        val label = graph.getProperty(GraphProperty.LABEL)
        val methodName = graph.getProperty(GraphProperty.METHOD_NAME)
        val hash = graph.getProperty(GraphProperty.HASH)
        val lineNumber = graph.getProperty(GraphProperty.LINE_NUMBER).toInt

        val originalCode = graph.getProperty(GraphProperty.ORIGINAL_CODE)
          .replace("\t", "  ")
          .split("\n")
          .toList

        val nodes = getNodes(graph)

        val astEdges = getEdges(graph, EdgeType.AST)
        val cfgEdges = getEdges(graph, EdgeType.CFG)
        val reachingDefEdges = getEdges(graph, EdgeType.REACHING_DEF)
        val cdgEdges = getEdges(graph, EdgeType.CDG)

        val jGraph = JGraph(filePath, label, methodName, lineNumber, originalCode, nodes,
            astEdges, cfgEdges, reachingDefEdges, cdgEdges)

        val jsonString = writePretty(jGraph)

        val directory = new File(outFolder)
        if (!directory.exists())
            directory.mkdir()

        val outFile = Paths.get(outFolder, s"$hash-$label.txt").toFile

        writeToFile(outFile, jsonString)
    }

    private def getEdges(graph: CGraph, edgeType: String): List[JEdge] = {
        graph.getEdges(edgeType).map(edge => JEdge(edge.from, edge.to))
    }

    private def getNodes(graph: CGraph): List[JNode] = {
        graph.nodes.map(node => JNode(node.id, node.nodeType, node.value))
    }

    private def writeToFile(file: File, content: String): Unit = {
        val printWriter = new PrintWriter(file)
        printWriter.write(content)
        printWriter.close()
    }
}

