package io.shiftleft.slice

import io.shiftleft.semanticcpg.language.toNodeTypeStarters
import io.shiftleft.slice.models.{GraphLabel, GraphProperty}
import io.shiftleft.slice.services.GraphBuilder

class GraphBuilderTests extends BaseTests {
  "Get graph" should "return a joern graph of the bad function" in {
    val method = cpg.method.find(m => m.name == "bad" && m.filename.endsWith("basic.c"))
    val graph = new GraphBuilder(cpg).build(method.get)

    assert(graph.get.nodes.size == 28)
    assert(graph.get.getProperty(GraphProperty.LABEL) == GraphLabel.BAD)
  }
}
