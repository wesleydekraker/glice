using Xunit;

namespace AstGenerator.Tests;

public class UnitTest1
{
    [Fact]
    public void Test1()
    {
        var code = File.ReadAllText("TestFiles/CWE23_Relative_Path_Traversal__Connect_tcp_11.cs");
        var graphs = GraphGenerator.GetGraphs("sample.cs", code).ToList();

        Assert.Equal(4, graphs.Count);

        Assert.Equal("Bad", graphs[0].MethodName);
        Assert.Equal("GoodG2B1", graphs[1].MethodName);
        Assert.Equal("GoodG2B2", graphs[2].MethodName);
        Assert.Equal("Good", graphs[3].MethodName);

        foreach (var node in graphs[0].Nodes)
        {
            Console.WriteLine(node.Id + ", " + node.NodeType + ", " + node.Value);
        }

        Console.WriteLine("AstEdges");
        foreach (var node in graphs[0].AstEdges)
        {
            Console.WriteLine(node.From + ", " + node.To);
        }

        Console.WriteLine("CfgEdges");
        foreach (var node in graphs[0].CfgEdges)
        {
            Console.WriteLine(node.From + ", " + node.To);
        }

        Console.WriteLine("ReachingDefEdges");
        foreach (var node in graphs[0].ReachingDefEdges)
        {
            Console.WriteLine(node.From + ", " + node.To);
        }

        Console.WriteLine("CdgEdges");
        foreach (var node in graphs[0].CdgEdges)
        {
            Console.WriteLine(node.From + ", " + node.To);
        }
    }
}