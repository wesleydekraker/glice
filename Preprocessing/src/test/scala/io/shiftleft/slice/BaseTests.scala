package io.shiftleft.slice

import io.shiftleft.codepropertygraph.Cpg
import io.shiftleft.slice.services.CpgGenerator
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec

import java.io.File

abstract class BaseTests extends AnyFlatSpec with BeforeAndAfterAll {
  protected val cpgFilePath = new File("src/test/resources/cpg.bin")
  protected val testResourcesFolder = new File("src/test/resources/testrepo")
  protected val joernPath = new File("joern-inst/joern-cli/joern-parse")
  var cpg: Cpg = _

  override def beforeAll(): Unit = {
    val cpgGenerator = new CpgGenerator(testResourcesFolder, cpgFilePath, joernPath)
    cpg = cpgGenerator.createCpg()
  }

  override def afterAll(): Unit = {
    cpg.close()
  }
}
