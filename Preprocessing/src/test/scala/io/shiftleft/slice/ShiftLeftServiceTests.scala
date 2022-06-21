package io.shiftleft.slice

import io.shiftleft.slice.services.ShiftLeftService

import java.io.File

class ShiftLeftServiceTests extends BaseTests {
  "Get graph" should "return the graphs in basic.c" in {
    val shiftLeftService = new ShiftLeftService(cpg)

    val file = new File("src/test/resources/testrepo/sard/basic.c")
    val graphs = shiftLeftService.getMethods(file)

    assert(graphs.size == 1)
    assert(graphs.head.name == "bad")
  }

  "Get graph" should "return the graphs in cdt.c" in {
    val shiftLeftService = new ShiftLeftService(cpg)

    val file = new File("src/test/resources/testrepo/sard/cdt.c")
    val graphs = shiftLeftService.getMethods(file)

    assert(graphs.size == 1)
    assert(graphs.head.name == "bad")
  }
}
