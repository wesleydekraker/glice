package io.shiftleft.slice.services

import io.shiftleft.codepropertygraph.Cpg
import io.shiftleft.slice.models.{CGraph, GraphProperty}
import io.shiftleft.slice.services.exports.ExportService

import java.io.File

class GraphService(val cpg: Cpg, val exportService: ExportService, val mode: String) {
    private val shiftLeftService = new ShiftLeftService(cpg)
    private val graphBuilder = new GraphBuilder(cpg)

    def run(): Unit = {
        val files = shiftLeftService.getFiles()

        for (originalFile <- files) {
            val slice = if (mode == "file") createFile(originalFile) else createMethods(originalFile)
            slice.foreach(slice => exportService.writeGraph(slice))
        }
    }

    private def createFile(originalFile: File): List[CGraph] = {
        val methods = this.shiftLeftService.getMethods(originalFile)

        val graphs = methods.flatMap(method => graphBuilder.build(method, Some(originalFile)))
          .sortBy(graph => graph.getProperty(GraphProperty.LINE_NUMBER).toInt)

        if (graphs.isEmpty)
            return List()

        val firstGraph = graphs.head

        for (graph <- graphs.tail) {
            firstGraph.appendGraph(graph)
        }

        List(firstGraph)
    }

    private def createMethods(originalFile: File): List[CGraph] = {
        val methods = this.shiftLeftService.getMethods(originalFile)
        val graphs = methods.flatMap(method => graphBuilder.build(method, Some(originalFile)))

        graphs
    }
}

