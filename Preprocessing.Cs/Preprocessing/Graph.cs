namespace AstGenerator;

public record Graph(string FilePath, string Label, string MethodName, int LineNumber, int Depth,
    List<string> OriginalCode, List<NodeData> Nodes,
    List<EdgeData> AstEdges, List<EdgeData> CfgEdges, List<EdgeData> CdgEdges, List<EdgeData> ReachingDefEdges);
