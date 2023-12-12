package org.example;

import java.util.HashMap;
import java.util.Map;

public class NodeType {
    private static final Map<String, String> map = new HashMap<>() {{
        put("MethodDeclaration", "declaration");
        put("AssignExpr", "operator_assignment");
        put("StringLiteralExpr", "string_literal");
        put("ReturnStmt", "return");
        put("NameExpr", "name");
        put("ExpressionStmt", "expression");
        put("ClassOrInterfaceType", "type");
        put("BlockStmt", "block");
        put("VariableDeclarationExpr", "variable_declaration");
        put("VariableDeclarator", "variable_declarator");
        put("VoidType", "void");
        put("IfStmt", "control_structure_if");
        put("BooleanLiteralExpr", "boolean_literal");
        put("PrimitiveType", "primitive_type");
        put("MethodCallExpr", "method_call");
        put("EnclosedExpr", "enclosed_expression");
        put("ObjectCreationExpr", "object_creation");
        put("IntegerLiteralExpr", "int_literal");
        put("BinaryExpr", "binary_expression");
        put("EmptyStmt", "empty_statement");
        put("BlockComment", "comment");
        put("Parameter", "method_parameter");
        put("ArrayType", "array_type");
        put("FieldAccessExpr", "operator_field_access");
        put("ForStmt", "control_structure_for");
        put("UnaryExpr", "unary_operator");
        put("WhileStmt", "control_structure_while");
        put("BreakStmt", "control_structure_break");
        put("TryStmt", "control_structure_try");
        put("CatchClause", "control_structure_catch");
        put("ForEachStmt", "control_structure_for_each");
        put("SwitchStmt", "control_structure_switch");
        put("ArrayAccessExpr", "operator_index_access");
        put("CastExpr", "operator_cast");
        put("NullLiteralExpr", "null_literal");
        put("ArrayCreationExpr", "operator_array_creation");
        put("ArrayInitializerExpr", "operator_array_initializer");
        put("ArrayCreationLevel", "array_creation_level");
        put("ThrowStmt", "control_structure_throw");
        put("ThisExpr", "this");
        put("SynchronizedStmt", "synchronized");
        put("LongLiteralExpr", "long_literal");
        put("ClassExpr", "class");
        put("DoStmt", "control_structure_do");
        put("DoubleLiteralExpr", "double_literal");
        put("SuperExpr", "super");
        put("CharLiteralExpr", "char_literal");
        put("AssertStmt", "assert");
    }};

    public static String get(String nodeType) {
        return map.getOrDefault(nodeType, camelToSnake(nodeType));
    }

    public static String camelToSnake(String camelCaseString) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < camelCaseString.length(); i++) {
            char character = camelCaseString.charAt(i);
            boolean isUpperCase = Character.isUpperCase(character);

            if (isUpperCase && i == 0) {
                result.append(Character.toLowerCase(character));
            } else if (isUpperCase) {
                result.append('_');
                result.append(Character.toLowerCase(character));
            } else {
                result.append(character);
            }
        }

        return result.toString();
    }
}
