package org.example;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.List;

public class MethodDeclarationVisitor extends VoidVisitorAdapter<Void> {
    private final List<Method> methods = new ArrayList<>();
    private ClassOrInterfaceDeclaration classDeclaration = null;

    @Override
    public void visit(MethodDeclaration methodDeclaration, Void arg) {
        methods.add(new Method(methodDeclaration, classDeclaration));
        super.visit(methodDeclaration, arg);
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration classOrInterfaceDeclaration, Void arg) {
        classDeclaration = classOrInterfaceDeclaration;
        super.visit(classOrInterfaceDeclaration, arg);
    }

    public List<Method> getMethods() {
        return methods;
    }
}
