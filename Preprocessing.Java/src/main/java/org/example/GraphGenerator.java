package org.example;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class GraphGenerator {
    public static List<Graph> generate(Path sourceCodeFile) throws IOException {
        CompilationUnit cu = StaticJavaParser.parse(sourceCodeFile);

        MethodDeclarationVisitor methodVisitor = new MethodDeclarationVisitor();
        methodVisitor.visit(cu, null);

        var methods = methodVisitor.getMethods();
        var graphs = new ArrayList<Graph>();

        for (Method method : methods) {
            var className = method.classDeclaration().getNameAsString();
            var methodDeclaration = method.declaration();
            var methodName = String.format("%s->%s", className, methodDeclaration.getNameAsString());

            int lineNumber = methodDeclaration.getBegin().map(position -> position.line).orElse(-1);

            var lines = readLines(sourceCodeFile);

            var graphVisitor = new AstVisitor();
            graphVisitor.process(methodDeclaration);

            graphs.add(new Graph(sourceCodeFile.toString(), "unknown", methodName, lineNumber, 0,
                    lines, graphVisitor.getNodes(),
                    graphVisitor.getAstEdges(), graphVisitor.getCfgEdges(), graphVisitor.getCdgEdges(),
                    graphVisitor.getReachingDefEdges()));
        }

        return graphs;
    }

    public static List<String> readLines(Path filePath) throws IOException {
        return Files.readAllLines(filePath, StandardCharsets.UTF_8);
    }
}
