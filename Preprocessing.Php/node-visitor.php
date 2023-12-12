<?php
require_once 'vendor/autoload.php';

use PhpParser\Node;
use PhpParser\NodeVisitorAbstract;

// Define a visitor class to extract the nodes and edges from the AST
class NodeVisitor extends NodeVisitorAbstract {
    private $nodes = array();
    private $astEdges = array();
    private $cfgEdges = array();
    private $reachingDefEdges = array();
    private $cdgEdges = array();
    private $next_id = 0;
    private $stack = array();
    private $cfgId = null;
    private $renamedVariables = array();
    private $variableNameNodeId = array();

    public function enterNode(Node $node) {
        // Generate a unique identifier for the node
        $id = $this->next_id++;

        // Extract the type and value of the node
        $type = $this->getNodeType($node);
        $value = $this->getNodeValue($node, $id);

        $nodeArray = array('id' => $id, 'nodeType' => $type, 'value' => $value);

        // Add the node to the list of nodes
        $this->nodes[] = $nodeArray;

        // If the node is not the root node, add an edge from its parent to itself
        if (!empty($this->stack)) {
            $parent_node = end($this->stack);

            $controlStatements = array("control_structure_if", "control_structure_else",
                "control_structure_while", "control_structure_for", "control_structure_foreach");

            if (in_array($parent_node['nodeType'], $controlStatements)) {
                $this->cdgEdges[] = array('from' => $parent_node['id'], 'to' => $id);
            }

            $parent_id = $parent_node['id'];
            $this->astEdges[] = array('from' => $parent_id, 'to' => $id);
        }

        if ($this->isStatement($node)) {
            if ($this->cfgId !== null) {
                $this->cfgEdges[] = array('from' => $this->cfgId, 'to' => $id);
            }

            $this->cfgId = $id;
        }

        // Push the node's ID onto the stack
        array_push($this->stack, $nodeArray);
    }

    public function leaveNode(Node $node) {
        // Pop the node's ID from the stack
        array_pop($this->stack);
    }

    private function isStatement($node) {
        return $node instanceof Node\Stmt;
    }

    private function getNodeType($node) {
        $typeName = $node->getType();

        // Determine node value based on node type
        switch ($typeName) {
            case 'Scalar_LNumber':
                return "int_literal";
            case 'Scalar_DNumber':
                return "float_literal";
            case 'Scalar_String':
                return "string_literal";
            case 'Expr_Variable':
                return "variable";
            case 'Identifier':
                return "identifier";
            case 'Expr_Assign':
                return "operator_assignment";
            case 'VarLikeIdentifier':
                return "var_like_identifier";
            case 'Stmt_Property':
                return "property_declaration";
            case 'Name':
                return "name";
            case 'Stmt_InlineHTML':
                return "inline_html_statement";
            case 'Arg':
                return "argument";
            case 'Expr_FuncCall':
                return "function_call";
            case 'Stmt_Else':
                return "control_structure_else";
            case 'Stmt_Expression':
                return "expression";
            case 'Stmt_If':
                return "control_structure_if";
            case 'Expr_ArrayDimFetch':
                return "operator_index_access";
            case 'Expr_Include':
                return "include";
            case 'Expr_New':
                return "object_creation";
            case 'Expr_MethodCall':
                return "method_call";
            case 'Stmt_Echo':
                return "echo";
            case 'Stmt_While':
                return "control_structure_while";
            case 'Stmt_ClassMethod':
                return 'method';
            case 'Expr_PropertyFetch':
                return 'operator_member_access';
            case 'Stmt_Return':
                return 'return';
            case 'Stmt_PropertyProperty':
                return 'property_property';
            case 'Stmt_Class':
                return 'class';
            case 'Stmt_For':
                return "control_structure_for";
            case 'Stmt_Foreach':
                return "control_structure_foreach";
            default:
                return $this->camelToSnake($typeName);
        }
    }

    private function camelToSnake($input) {
        $output = '';
    
        for ($i = 0; $i < strlen($input); $i++) {
            $char = $input[$i];

            if ($char === '_') {
                continue;
            }
    
            if ($i > 0 && ctype_upper($char)) {
                $output .= '_';
            }

            $output .= strtolower($char);
        }
    
        return $output;
    }

    private function getNodeValue($node, $id) {
        $typeName = $node->getType();

        // Determine node value based on node type
        switch ($typeName) {
            case 'Scalar_LNumber': // integer literals
            case 'Scalar_DNumber': // floating-point literals
                return strval($node->value);
            case 'Scalar_String': // "Hello, world!"
                return $node->value;
            case 'Scalar_EncapsedStringPart';
                return $node->value;
            case 'Expr_Variable': // $variable
                $variableName = $node->name;
                $newVariableName = isset($this->renamedVariables[$variableName]) 
                    ? $this->renamedVariables[$variableName] 
                    : $variableName;
                
                if (isset($this->variableNameNodeId[$newVariableName])) {
                    $from = $this->variableNameNodeId[$newVariableName];
                    $this->reachingDefEdges[] = array('from' => $from, 'to' => $id);
                }

                return $newVariableName;
            case 'Identifier':
                return $node->name;
            case 'Expr_Assign':
                if ($node->var->getType() !== "Expr_Variable") {
                    return "";
                }

                $variableName = $node->var->name;

                if (!isset($this->renamedVariables[$variableName])) {
                    $this->renamedVariables[$variableName] = "variable_" . count($this->renamedVariables);
                }
                
                $newVariableName = $this->renamedVariables[$variableName];
                $this->variableNameNodeId[$newVariableName] = $id;

                return "";
            case 'VarLikeIdentifier': // variable, parameter or property 
                return $node->name;
            case 'Stmt_Property': // property declaration in a class
                $visibility = 'public';
                if ($node->isProtected()) {
                    $visibility = 'protected';
                } elseif ($node->isPrivate()) {
                    $visibility = 'private';
                }

                return $visibility;
            case 'Name':
                return implode(",", $node->parts);
            case 'Stmt_InlineHTML':
                $no_comments = preg_replace('/<!--(.*?)-->/s', '', $node->value);
                return $no_comments;
            default:
                return "";
        }
    }

    public function getNodes() {
        return $this->nodes;
    }

    public function getAstEdges() {
        return $this->astEdges;
    }

    public function getCfgEdges() {
        return $this->cfgEdges;
    }

    public function getCdgEdges() {
        return $this->cdgEdges;
    }

    public function getReachingDefEdges() {
        return $this->reachingDefEdges;
    }
}

?>
