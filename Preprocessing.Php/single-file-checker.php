<?php
function checkSrcFolder($dirPath) {
    $dirIter = new DirectoryIterator($dirPath);

    $i = 0;

    foreach ($dirIter as $path) {
        if ($path->isDot() || $path->isFile()) {
            continue;
        }
        $i++;

        $srcPath = $path->getPathname()  . DIRECTORY_SEPARATOR . "src";

        $dirIter = new RecursiveDirectoryIterator($srcPath, RecursiveDirectoryIterator::SKIP_DOTS);
        $iter = new RecursiveIteratorIterator($dirIter, RecursiveIteratorIterator::SELF_FIRST);
    
        $phpFileCount = 0;
    
        foreach ($iter as $path) {
            if ($path->isFile() && $path->getExtension() === 'php') {
                $phpFileCount += 1;
            }
        }
    
        if ($phpFileCount !== 1) {
            throw new Exception("Invalid PHP file count in 'src' folder: " . $path->getPathname());
        }
    }

    echo $i . PHP_EOL;
}

try {
    checkSrcFolder('/home/php/dataset/');
} catch (Exception $e) {
    echo 'Error: ' . $e->getMessage();
}