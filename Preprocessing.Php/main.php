<?php
require_once 'vendor/autoload.php';
require_once 'filesystem.php';
require_once 'parser.php';

function help() {
    echo "Usage: php main.php [input_path] [output_path]\n";
    echo "input_path: The path to the directory containing PHP files to process.\n";
    echo "output_path: The path to the directory where output graphs will be written.\n";
}

function processPhpFiles($inputPath, $outputPath) {
    $phpFiles = listPhpFiles($inputPath);

    foreach ($phpFiles as $phpFile) {
        try {
            $json = parse($phpFile);
        } catch (PhpParser\Error $e) {
            echo $phpFile . PHP_EOL;
            echo $e->getMessage() . PHP_EOL;
            continue;
        }

        $jsonText = json_encode($json, JSON_PRETTY_PRINT | JSON_UNESCAPED_SLASHES);

        $sha512Hash = strtoupper(hash("sha256", join(PHP_EOL, $json["originalCode"])));
        $fileName = $sha512Hash . "-" . $json["label"] . ".txt";

        $outputFile = rtrim($outputPath, DIRECTORY_SEPARATOR) . DIRECTORY_SEPARATOR . $fileName;

        file_put_contents($outputFile, $jsonText);
    }
}

if (count($argv) != 3) {
    help();
    exit(1);
}

$inputPath = realpath($argv[1]);
$outputPath = realpath($argv[2]);

if ($inputPath === false || !is_dir($inputPath)) {
    echo "Error: Input path is not a valid directory." . PHP_EOL;
    help();
    exit(1);
}

if ($outputPath === false || !is_dir($outputPath)) {
    echo "Error: Output path is not a valid directory." . PHP_EOL;
    help();
    exit(1);
}

processPhpFiles($inputPath, $outputPath);

?>
