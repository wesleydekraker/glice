package io.shiftleft.slice.services

import io.shiftleft.codepropertygraph.Cpg
import io.shiftleft.slice.models.{CGraph, GraphProperty}
import io.shiftleft.slice.services.exports.ExportService

import java.io.File

class GraphService(val cpg: Cpg, val exportService: ExportService, val targetDepth: Int, val createSlices: Boolean) {
    private val shiftLeftService = new ShiftLeftService(cpg)
    private val graphBuilder = new GraphBuilder(cpg)

    def run(): Unit = {
        val files = shiftLeftService.getFiles()

        for (originalFile <- files) {
            val slice = if (createSlices) createSlice(originalFile) else createGraphs(originalFile)
            slice.foreach(slice => exportService.writeGraph(slice))
        }
    }

    private def createSlice(originalFile: File): List[CGraph] = {
        val methods = this.shiftLeftService.getMethods(originalFile)

        val graphs = methods.flatMap(method => graphBuilder.build(method))
          .sortBy(graph => graph.getProperty(GraphProperty.LINE_NUMBER).toInt)

        if (graphs.isEmpty)
            return List()

        val firstGraph = graphs.head

        for (graph <- graphs.tail.take(targetDepth)) {
            firstGraph.appendGraph(graph)
        }

        firstGraph.setProperty(GraphProperty.DEPTH, (graphs.length - 1).toString)

        List(firstGraph)
    }

    private def createGraphs(originalFile: File): List[CGraph] = {
        val methods = this.shiftLeftService.getMethods(originalFile)
        val graphs = methods.flatMap(method => graphBuilder.build(method))
        graphs.foreach(graph => graph.setProperty(GraphProperty.DEPTH, 0.toString))

        graphs
    }
}

