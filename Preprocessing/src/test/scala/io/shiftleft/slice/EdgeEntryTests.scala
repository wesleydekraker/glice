package io.shiftleft.slice

import io.shiftleft.slice.models.CEdge
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec

class EdgeEntryTests extends AnyFlatSpec with BeforeAndAfterAll {
  "Equals" should "returns true if the edge entries are the same otherwise false" in {
    val edgeEntry = CEdge(0, 1, "AST")

    assert(edgeEntry == CEdge(0, 1, "AST"))
    assert(edgeEntry != CEdge(1, 1, "AST"))
    assert(edgeEntry != CEdge(0, 0, "AST"))
    assert(edgeEntry != CEdge(1, 0, "AST"))
  }
}
