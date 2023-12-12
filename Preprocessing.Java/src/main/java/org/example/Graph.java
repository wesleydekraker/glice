package org.example;

import java.util.List;

public record Graph(String filePath, String label, String methodName, int lineNumber, int depth,
                    List<String> originalCode, List<NodeData> nodes,
                    List<EdgeData> astEdges, List<EdgeData> cfgEdges, List<EdgeData> cdgEdges,
                    List<EdgeData> reachingDefEdges) {
}
