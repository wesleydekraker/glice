<?php
require_once 'vendor/autoload.php';
require_once 'parser.php';

$json = parse("./sample/example0.php");
$jsonText = json_encode($json, JSON_PRETTY_PRINT);
echo $jsonText . PHP_EOL;
$sha512Hash = strtoupper(hash("sha256", join(PHP_EOL, $json["originalCode"])));
$fileName = $sha512Hash . "-" . $json["label"] . ".txt";
echo $fileName . PHP_EOL;

?>
