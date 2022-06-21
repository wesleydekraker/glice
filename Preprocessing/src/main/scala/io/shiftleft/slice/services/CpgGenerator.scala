package io.shiftleft.slice.services

import io.shiftleft.codepropertygraph.Cpg

import java.io.File
import scala.language.postfixOps
import sys.process._

class CpgGenerator(val sourceCodePath: File, val cpgFilePath: File,
                   val joernPath: File = new File("Preprocessing/joern-inst/joern-cli/joern-parse")) {
  private val logger = new LogService()

  def createCpg(overwriteExistingCpg: Boolean = false): Cpg = {
    if (!sourceCodePath.exists()) {
      logger.error(s"Source code path does not exist: ${sourceCodePath.getAbsolutePath}.")
      System.exit(1)
    }

    if (!sourceCodePath.isDirectory) {
      logger.error(s"Source code path should be a directory: ${sourceCodePath.getAbsolutePath}.")
      System.exit(1)
    }

    if (!joernPath.exists() || !joernPath.isFile) {
      logger.error(s"The file joern-parse does not exist at: ${joernPath.getAbsolutePath}.")
      System.exit(1)
    }

    val lastModifiedTestResource = getLastModifiedFile(sourceCodePath)

    if (overwriteExistingCpg || lastModifiedTestResource.lastModified > cpgFilePath.lastModified)
      s"timeout 10m $joernPath $sourceCodePath --out $cpgFilePath" !

    Cpg.withStorage(cpgFilePath.getAbsolutePath)
  }

  private def getLastModifiedFile(path: File): File = {
    val files = listFiles(path) :+ path
    files.maxBy(_.lastModified)
  }

  private def listFiles(f: File): Array[File] = {
    val files = f.listFiles
    files ++ files.filter(_.isDirectory).flatMap(listFiles)
  }
}
