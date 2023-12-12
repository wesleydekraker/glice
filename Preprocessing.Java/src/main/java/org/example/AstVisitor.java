package org.example;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.PrimitiveType;

import java.util.*;

public class AstVisitor {
    private int nodeId = 0;
    private final List<NodeData> nodes = new ArrayList<>();
    private final List<EdgeData> astEdges = new ArrayList<>();
    private final List<EdgeData> cfgEdges = new ArrayList<>();
    private final List<EdgeData> cdgEdges = new ArrayList<>();
    private final List<EdgeData> reachingDefEdges = new ArrayList<>();
    private Integer cfgId = null;
    private final Stack<NodeData> nodeStack = new Stack<>();
    private final HashMap<String, String> renamedVariables = new HashMap<>();
    private final HashMap<String, Integer> variableNameNodeId = new HashMap<>();

    private static final List<String> commentClassNames = new ArrayList<>(Arrays.asList("BlockComment", "LineComment",
                                                                                        "JavadocComment"));

    public void process(Node node) {
        String className = node.getClass().getSimpleName();

        if (commentClassNames.contains(className)) {
            return;
        }

        int currentNodeId = nodeId++;
        String type = NodeType.get(className);
        String value = getNodeValue(node, currentNodeId);

        var nodeData = new NodeData(currentNodeId, type, value);
        nodes.add(nodeData);

        if (!nodeStack.isEmpty()) {
            NodeData parentNode = nodeStack.peek();

            var parentControlStatement = getParentControlStatement();
            if (parentControlStatement != null) {
                cdgEdges.add(new EdgeData(parentControlStatement.id(), currentNodeId));
            }

            astEdges.add(new EdgeData(parentNode.id(), currentNodeId));
        }

        nodeStack.push(nodeData);

        if (isStatement(node)) {
            if (cfgId != null) {
                cfgEdges.add(new EdgeData(cfgId, currentNodeId));
            }

            cfgId = currentNodeId;
        }

        for (Node child : node.getChildNodes()) {
            process(child);
        }

        nodeStack.pop();
    }

    private static boolean isStatement(Node node) {
        return node instanceof Statement;
    }

    private NodeData getParentControlStatement() {
        if (nodeStack.size() == 0) {
            return null;
        }

        var nodeTop = nodeStack.peek();

        if (isControlStatement(nodeTop)) {
            return nodeTop;
        } else if (nodeStack.size() >= 2){
            var nodeBeforeTop = nodeStack.get(nodeStack.size() - 2);
            return isBlock(nodeTop) && isControlStatement(nodeBeforeTop) ? nodeBeforeTop : null;
        } else {
            return null;
        }
    }

    private static boolean isControlStatement(NodeData nodeData) {
        var controlStatements = List.of("control_structure_if", "control_structure_while",
                "control_structure_for", "control_structure_for_each");
        return controlStatements.contains(nodeData.nodeType());
    }

    private static boolean isBlock(NodeData nodeData) {
        return nodeData.nodeType().equals("block");
    }

    private String getNodeValue(Node node, Integer id) {
        String className = node.getClass().getSimpleName();

        return switch (className) {
            case "Name" -> ((Name) node).getIdentifier();
            case "SimpleName" -> simpleNameToValue((SimpleName)node, id);
            case "NameExpr" -> ((NameExpr) node).getNameAsString();
            case "IntegerLiteralExpr" -> ((IntegerLiteralExpr) node).getValue();
            case "LongLiteralExpr" -> ((LongLiteralExpr) node).getValue();
            case "DoubleLiteralExpr" -> ((DoubleLiteralExpr) node).getValue();
            case "BooleanLiteralExpr" -> String.valueOf(((BooleanLiteralExpr) node).getValue());
            case "CharLiteralExpr" -> ((CharLiteralExpr) node).getValue();
            case "StringLiteralExpr" -> ((StringLiteralExpr) node).getValue();
            case "NullLiteralExpr" -> "null";
            case "Modifier" -> ((Modifier) node).getKeyword().name();
            case "PrimitiveType" -> ((PrimitiveType) node).asString();
            case "BinaryExpr" -> ((BinaryExpr) node).getOperator().name();
            default -> "";
        };
    }

    private String simpleNameToValue(SimpleName node, Integer id) {
        var variableName = node.getIdentifier();
        var parentIsVariableDeclarator = parentIsVariableDeclarator();

        String newVariableName;

        if (parentIsVariableDeclarator && !renamedVariables.containsKey(variableName)) {
            newVariableName = "variable_" + renamedVariables.size();
            renamedVariables.put(variableName, newVariableName);
            variableNameNodeId.put(newVariableName, id);
        } if (parentIsVariableDeclarator && renamedVariables.containsKey(variableName)) {
            newVariableName = renamedVariables.get(variableName);
            variableNameNodeId.put(newVariableName, id);
        } else {
            newVariableName = renamedVariables.getOrDefault(variableName, variableName);

            if (variableNameNodeId.containsKey(newVariableName)) {
                var from = variableNameNodeId.get(newVariableName);
                reachingDefEdges.add(new EdgeData(from, id));
            }
        }
        return newVariableName;
    }

    private boolean parentIsVariableDeclarator() {
        if (nodeStack.empty()) {
            return false;
        } else {
            return nodeStack.peek().nodeType().equals("variable_declarator");
        }
    }

    public List<NodeData> getNodes() {
        return nodes;
    }

    public List<EdgeData> getAstEdges() {
        return astEdges;
    }

    public List<EdgeData> getCfgEdges() {
        return cfgEdges;
    }

    public List<EdgeData> getCdgEdges() {
        return cdgEdges;
    }

    public List<EdgeData> getReachingDefEdges() {
        return reachingDefEdges;
    }
}