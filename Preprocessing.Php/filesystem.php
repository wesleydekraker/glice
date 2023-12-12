<?php

function listPhpFiles($directory) {
    // Create a RecursiveDirectoryIterator for the given directory
    $directoryIterator = new RecursiveDirectoryIterator($directory, FilesystemIterator::SKIP_DOTS);

    // Iterate through the directory and its subdirectories using RecursiveIteratorIterator
    $iterator = new RecursiveIteratorIterator($directoryIterator, RecursiveIteratorIterator::SELF_FIRST);

    // Loop through the files and directories
    foreach ($iterator as $fileInfo) {
        // Check if it's a file with a .php extension
        if ($fileInfo->isFile() && $fileInfo->getExtension() === 'php') {
            // Add the file to the list
            yield $fileInfo->getPathname();
        }
    }
}

?>
