package org.example;

import com.github.javaparser.ParseProblemException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        if (args.length != 2) {
            displayHelp();
            return;
        }

        Path inputPath;
        Path outputPath;

        try {
            inputPath = Paths.get(args[0]);
            outputPath = Paths.get(args[1]);
        } catch (InvalidPathException e) {
            log("Invalid path: " + e.getMessage());
            return;
        }

        if (!Files.isDirectory(inputPath) || !Files.isDirectory(outputPath)) {
            log("Input or output path does not exist.");
            return;
        }

        getJavaFiles(inputPath).parallelStream().forEach(inputFile -> {
            try {
                writeToDisk(inputFile, outputPath);
            } catch (IOException | ParseProblemException e) {
                log(inputFile.toString(), e.getMessage());
            }
        });
    }

    private static void writeToDisk(Path inputFile, Path outputPath) throws IOException {
        var graphs = GraphGenerator.generate(inputFile);

        for (Graph graph : graphs) {
            var content = readString(inputFile);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String graphAsJson = gson.toJson(graph);

            var hash = generateSHA256Hash(content, graph.methodName());
            var fileName = String.format("%s-depth0-%s.txt", hash, graph.label());

            Path outputFile = outputPath.resolve(fileName).toAbsolutePath();
            Files.writeString(outputFile, graphAsJson);
        }
    }

    public static String readString(Path filePath) throws IOException {
        return Files.readString(filePath, StandardCharsets.UTF_8);
    }

    public static String generateSHA256Hash(String... input) {
        var inputString = String.join("", input);

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(inputString.getBytes(StandardCharsets.UTF_8));
            return IntStream.range(0, hashBytes.length)
                    .mapToObj(i -> String.format("%02x", hashBytes[i]))
                    .collect(Collectors.joining());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating SHA-256 hash", e);
        }
    }

    private static List<Path> getJavaFiles(Path folderPath) {
        try (Stream<Path> paths = Files.walk(folderPath)) {
            return paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("Error walking file tree: " + e.getMessage());
        }
    }

    private static void displayHelp() {
        log("Usage: java GraphGenerator <inputPath> <outputPath>",
                "  inputPath:  Path to the Java files to be parsed",
                "  outputPath: Path to write graph output to");
    }

    private static synchronized void log(String... messages) {
        for (String message : messages) {
            System.out.println(message);
        }
    }
}