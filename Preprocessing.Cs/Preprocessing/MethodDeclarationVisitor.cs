using Microsoft.CodeAnalysis.CSharp;
using Microsoft.CodeAnalysis.CSharp.Syntax;

namespace AstGenerator;

public class MethodDeclarationVisitor : CSharpSyntaxWalker
{
    public List<Method> MethodDeclarations { get; } = new List<Method>();
    private ClassDeclarationSyntax? classDeclaration { get; set; } = null;


    public override void VisitMethodDeclaration(MethodDeclarationSyntax node)
    {
        MethodDeclarations.Add(new Method(node, classDeclaration));
        base.VisitMethodDeclaration(node);
    }

    public override void VisitClassDeclaration(ClassDeclarationSyntax node)
    {
        classDeclaration = node;
        base.VisitClassDeclaration(node);
    }
}