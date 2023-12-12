package io.shiftleft.slice

import io.shiftleft.slice.services.exports.ExportService
import io.shiftleft.slice.models.{CEdge, CGraph, CNode, EdgeType, GraphProperty, NodeType}

import java.io.File

class ExportServiceTests extends BaseTests {
  "Write" should "export to correct format" in {
    val nodes = List(CNode(0, NodeType.METHOD, "A"), CNode(1, NodeType.BLOCK))
    val edges = List(
      CEdge(0, 0, EdgeType.AST), CEdge(1, 1, EdgeType.AST),
      CEdge(0, 0, EdgeType.CFG),
      CEdge(0, 0, EdgeType.REACHING_DEF),
      CEdge(0, 1, EdgeType.CDG)
    )

    val graph = new CGraph(nodes.head.id)
    graph.appendNodes(nodes)
    graph.appendEdges(edges)

    graph.setProperty(GraphProperty.LABEL, "unknown")
    graph.setProperty(GraphProperty.METHOD_NAME, "function")
    graph.setProperty(GraphProperty.LINE_NUMBER, 1.toString)
    graph.setProperty(GraphProperty.ORIGINAL_CODE, "function x\n{\n}\n")
    graph.setProperty(GraphProperty.HASH, "HASH")

    var fileContent = ""
    val writeToFile = (_: File, content: String) => fileContent = content

    val exportService = new ExportService("graphs")
    exportService.writeGraph(graph, writeToFile)

    print(fileContent)

    assert(fileContent ==
      """{
        |  "filePath":"",
        |  "label":"unknown",
        |  "methodName":"function",
        |  "lineNumber":1,
        |  "originalCode":[
        |    "function x",
        |    "{",
        |    "}"
        |  ],
        |  "nodes":[
        |    {
        |      "id":0,
        |      "nodeType":"method",
        |      "value":"A"
        |    },
        |    {
        |      "id":1,
        |      "nodeType":"block",
        |      "value":""
        |    }
        |  ],
        |  "astEdges":[
        |    {
        |      "from":0,
        |      "to":0
        |    },
        |    {
        |      "from":1,
        |      "to":1
        |    }
        |  ],
        |  "cfgEdges":[
        |    {
        |      "from":0,
        |      "to":0
        |    }
        |  ],
        |  "reachingDefEdges":[
        |    {
        |      "from":0,
        |      "to":0
        |    }
        |  ],
        |  "cdgEdges":[
        |    {
        |      "from":0,
        |      "to":1
        |    }
        |  ]
        |}""".stripMargin)
  }
}
