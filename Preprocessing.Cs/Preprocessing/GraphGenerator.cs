using System.Text.RegularExpressions;
using Microsoft.CodeAnalysis.CSharp;

namespace AstGenerator;

public class GraphGenerator
{
    public static IEnumerable<Graph> GetGraphs(string fileName, string code)
    {
        var tree = CSharpSyntaxTree.ParseText(code);
        var root = tree.GetRoot();

        var methodVisitor = new MethodDeclarationVisitor();
        methodVisitor.Visit(root);

        var methodDeclarations = methodVisitor.MethodDeclarations;

        foreach (var method in methodDeclarations)
        {
            var methodDeclaration = method.Declaration;

            var className = method.ClassDeclaration?.Identifier.ToString();
            var methodName = methodDeclaration.Identifier.ToString();
            var fullName = className == null ? methodName : $"{className}->{methodName}";

            var lineNumber = methodDeclaration.GetLocation().GetLineSpan().StartLinePosition.Line;

            var lines = ConvertToLines(code);

            var graphVisitor = new AstVisitor();
            graphVisitor.Process(methodDeclaration);

            yield return new Graph(fileName, "unknown", fullName, lineNumber, 0,
                lines, graphVisitor.GetNodes(),
                graphVisitor.GetAstEdges(), graphVisitor.GetCfgEdges(), graphVisitor.GetCdgEdges(),
                graphVisitor.getReachingDefEdges());
        }
    }

    public static List<string> ConvertToLines(string text)
    {
        string[] separators = { "\r\n", "\n" };
        return new List<string>(Regex.Split(text, string.Join("|", separators)));
    }
}
