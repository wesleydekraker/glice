using System.Text;
using Microsoft.CodeAnalysis;
using Microsoft.CodeAnalysis.CSharp;
using Microsoft.CodeAnalysis.CSharp.Syntax;

namespace AstGenerator;

public class AstVisitor
{
    private int _nodeId = 0;
    private readonly List<NodeData> _nodes = new();
    private readonly List<EdgeData> _astEdges = new();
    private readonly List<EdgeData> _cfgEdges = new();
    private readonly List<EdgeData> _cdgEdges = new();
    private readonly List<EdgeData> _reachingDefEdges = new();
    private readonly Dictionary<string, int> _variableNameNodeId = new();
    private readonly Stack<int> _astStack = new();
    private int? _cfgId = null;
    private readonly Dictionary<string, string> _renamedVariables = new();

    public void Process(SyntaxNode node, int? parentControlStatementId = null)
    {
        int currentNodeId = _nodeId++;
        string type = GetNodeType(node);
        string value = GetNodeValue(node, currentNodeId);

        _nodes.Add(new NodeData(currentNodeId, type, value));

        if (_astStack.Count > 0)
        {
            int parentNodeId = _astStack.Peek();
            _astEdges.Add(new EdgeData(parentNodeId, currentNodeId));
        }

        _astStack.Push(currentNodeId);

        if (IsBlockOrStatement(node))
        {
            if (_cfgId.HasValue)
            {
                _cfgEdges.Add(new EdgeData(_cfgId.Value, currentNodeId));
            }

            _cfgId = currentNodeId;
        }

        int? controlStatementId = null;
        if (node is IfStatementSyntax || node is WhileStatementSyntax || node is ForStatementSyntax)
        {
            controlStatementId = currentNodeId;
        }
        else if (parentControlStatementId != null && node is BlockSyntax)
        {
            controlStatementId = parentControlStatementId;
        }

        if (parentControlStatementId != null)
        {
            _cdgEdges.Add(new EdgeData(parentControlStatementId.Value, currentNodeId));
        }

        foreach (var child in node.ChildNodes())
        {
            Process(child, controlStatementId);
        }

        _astStack.Pop();
    }

    private bool IsBlockOrStatement(SyntaxNode node)
    {
        return node is StatementSyntax || node is BlockSyntax;
    }

    private string GetNodeType(SyntaxNode node)
    {
        switch (node)
        {
            case LiteralExpressionSyntax:
                return "literal";
            case MethodDeclarationSyntax:
                return "method";
            case CatchClauseSyntax:
                return "control_structure_catch_clause";
            case CatchDeclarationSyntax:
                return "control_structure_catch_declaration";
            case IfStatementSyntax:
                return "control_structure_if";
            case ElseClauseSyntax:
                return "control_structure_else";
            case SwitchStatementSyntax:
                return "control_structure_switch";
            default:
                var nodeType = node.GetType().Name.Replace("Syntax", "");
                return PascalCaseToSnakeCase(nodeType);
        }
    }

    private string GetNodeValue(SyntaxNode node, int currentNodeId)
    {
        switch(node)
        {
            case MethodDeclarationSyntax methodNode:
                return methodNode.Identifier.ValueText;
            case BinaryExpressionSyntax binaryExpressionNode:
                return PascalCaseToSnakeCase(binaryExpressionNode.Kind().ToString());
            case LiteralExpressionSyntax literalNode:
                return literalNode.Token.ValueText;
            case ParameterSyntax parameterSyntax:
                return parameterSyntax.Identifier.ValueText;
            case IdentifierNameSyntax identifierNode:
                var identifierName = identifierNode.Identifier.Text;

                var newIdentifierName = _renamedVariables.ContainsKey(identifierName)
                    ?  _renamedVariables[identifierName] : identifierName;
                
                if (_variableNameNodeId.ContainsKey(newIdentifierName))
                {
                    var from = _variableNameNodeId[newIdentifierName];
                    _reachingDefEdges.Add(new EdgeData(from, currentNodeId));
                }

                return newIdentifierName;
            case PredefinedTypeSyntax predefinedTypeNode:
                return predefinedTypeNode.Keyword.Text;
            case VariableDeclaratorSyntax variableDeclaratorNode:
                var declaratorName = variableDeclaratorNode.Identifier.Text;

                if (!_renamedVariables.ContainsKey(declaratorName))
                {
                    _renamedVariables[declaratorName] = $"variable_{_renamedVariables.Count}";
                }

                var newDeclaratorName = _renamedVariables[declaratorName];

                _variableNameNodeId[newDeclaratorName] = currentNodeId;

                return newDeclaratorName;
            case GenericNameSyntax genericNameNode:
                return genericNameNode.Identifier.ValueText;
            default:
                return "";
        }
    }

    private static string PascalCaseToSnakeCase(string input)
    {
        if (string.IsNullOrEmpty(input))
        {
            return input;
        }
        
        var output = new StringBuilder();
        for (int i = 0; i < input.Length; i++)
        {
            char currentChar = input[i];
            if (i > 0 && char.IsUpper(currentChar))
            {
                output.Append("_");
            }
            output.Append(char.ToLower(currentChar));
        }
        
        return output.ToString();
    }

    public List<NodeData> GetNodes()
    {
        return _nodes;
    }

    public List<EdgeData> GetAstEdges()
    {
        return _astEdges;
    }

    public List<EdgeData> GetCfgEdges()
    {
        return _cfgEdges;
    }

    public List<EdgeData> GetCdgEdges()
    {
        return _cdgEdges;
    }

    public List<EdgeData> getReachingDefEdges()
    {
        return _reachingDefEdges;
    }
}