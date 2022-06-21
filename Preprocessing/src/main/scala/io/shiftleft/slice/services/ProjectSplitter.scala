package io.shiftleft.slice.services

import java.io.File
import java.nio.file.Files
import scala.collection.mutable

class ProjectSplitter {
  private def cFileExtensions = List(".c", ".cc", ".cpp", ".o")
  private def maxSpace = 5 * 1000 * 1000

  def run(sourceFile: File): List[File] = {
    val files = getFiles(sourceFile)

    var currentSpace = 0L
    var index = 0
    val folders = mutable.Set[String]()

    for (file <- files) {
      if (currentSpace > maxSpace) {
        currentSpace = 0L
        index += 1
      }

      val newFolder = "source-%05d".format(index)
      folders += newFolder

      val newFile = new File(file.getAbsolutePath.replace("source", newFolder))

      Files.createDirectories(newFile.getParentFile.toPath)
      Files.copy(file.toPath, newFile.toPath)

      currentSpace += Files.size(file.toPath)
    }

    folders.map(folder => new File(folder)).toList
  }

  private def getFiles(directory: File): List[File] = {
    val fileEntries = directory.listFiles()

    val directories = fileEntries.filter(file => file.isDirectory).toList
    val files = fileEntries.filter(file => file.isFile && isCFile(file)).toList

    directories.flatMap(folder => getFiles(folder)) ++ files
  }

  private def isCFile(file: File): Boolean = {
    val filename = file.getName
    cFileExtensions.exists(cFileExtension => filename.endsWith(cFileExtension))
  }
}

