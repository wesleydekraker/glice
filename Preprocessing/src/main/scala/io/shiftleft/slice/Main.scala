package io.shiftleft.slice

import io.shiftleft.slice.services.exports.ExportService
import io.shiftleft.slice.services.{CpgGenerator, GraphService, ProjectSplitter}

import java.io.File
import scala.reflect.io.Directory

object Main {
  def main(args: Array[String]): Unit = {
    if (args.length != 3)
      throw new Exception("Three arguments required: outFolder, slice/func, targetDepth.")

    val outFolder = args(0)
    val createSlices = args(1).contains("slice")
    val targetDepth = args(2).toInt

    val graphExportService = new ExportService(outFolder)

    val projectSplitter = new ProjectSplitter()
    val folders = projectSplitter.run(new File("source"))

    for (folder <- folders) {
      try {
        val cpgFilePath = new File("cpg.bin")

        val cpgGenerator = new CpgGenerator(folder, cpgFilePath)
        val cpg = cpgGenerator.createCpg(overwriteExistingCpg = true)

        val graphService = new GraphService(cpg, graphExportService, targetDepth, createSlices)
        graphService.run()

        cpg.close()

        new Directory(folder).deleteRecursively()
      } catch {
        case e: Throwable => e.printStackTrace()
      }
    }
  }
}
