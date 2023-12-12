<?php
require_once 'vendor/autoload.php';
require_once 'node-visitor.php';
require_once 'filesystem.php';

use PhpParser\NodeTraverser;
use PhpParser\ParserFactory;

// Parse the PHP code and traverse the resulting AST with our visitor
$parser = (new ParserFactory)->create(ParserFactory::PREFER_PHP7);

function parse($phpFile) {
    global $parser;

    $code = file_get_contents($phpFile);

    $stmts = $parser->parse($code);
    
    $traverser = new NodeTraverser;
    $visitor = new NodeVisitor;
    $traverser->addVisitor($visitor);
    $traverser->traverse($stmts);

    // Convert the list of nodes and edges to JSON format
    $json = array(
        'filePath' => $phpFile,
        'label' => "unknown",
        'methodName' => 'all',
        'lineNumber' => 0,
        'depth' => 0,
        'originalCode' => preg_split("/\r\n|\n|\r/", $code),
        'nodes' => $visitor->getNodes(),
        'astEdges' => $visitor->getAstEdges(),
        'cfgEdges' => $visitor->getCfgEdges(),
        'cdgEdges' => $visitor->getCdgEdges(),
        'reachingDefEdges' => $visitor->getReachingDefEdges()
    );

    // Output the JSON
    return $json;
}

?>
