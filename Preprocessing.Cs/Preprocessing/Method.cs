using Microsoft.CodeAnalysis.CSharp.Syntax;

namespace AstGenerator;

public record Method(MethodDeclarationSyntax Declaration, ClassDeclarationSyntax? ClassDeclaration);
