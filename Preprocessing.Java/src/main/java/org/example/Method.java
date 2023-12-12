package org.example;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

public record Method (MethodDeclaration declaration, ClassOrInterfaceDeclaration classDeclaration) {}
