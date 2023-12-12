using System.Security.Cryptography;
using System.Text;
using System.Text.Json;

namespace AstGenerator;

public class Program
{
    private static readonly object _logLock = new object();

    private static JsonSerializerOptions JsonOptions = new JsonSerializerOptions
    {
        WriteIndented = true,
        PropertyNamingPolicy = JsonNamingPolicy.CamelCase
    };

    public static void Main(string[] args)
    {      
        if (args.Length != 2)
        {
            ShowHelp();
            return;
        }

        string inputPath = args[0];
        string outputPath = args[1];

        if (!Directory.Exists(inputPath))
        {
            Log($"Error: Input path '{inputPath}' does not exist or is not a directory.");
            return;
        }

        if (!Directory.Exists(outputPath))
        {
            Log($"Error: Output path '{outputPath}' does not exist or is not a directory.");
            return;
        }

        ProcessFiles(inputPath, outputPath);       
    }

    private static void ProcessFiles(string inputPath, string outputPath)
    {
        var csFiles = GetCsFiles(inputPath);

        Parallel.ForEach(csFiles, sourceCodeFile =>
        {
            try
            {
                var code = File.ReadAllText(sourceCodeFile);
                var graphs = GraphGenerator.GetGraphs(sourceCodeFile, code);

                foreach (var graph in graphs)
                {
                    string graphAsJson = JsonSerializer.Serialize(graph, JsonOptions);

                    var hash = GenerateSHA256Hash(code, graph.MethodName);

                    var path = Path.Combine(outputPath, $"{hash}-depth0-{graph.Label}.txt");
                    File.WriteAllText(path, graphAsJson);
                }
            }
            catch (IOException e)
            {
                Log(sourceCodeFile, e.Message);
            }
        });
    }

    private static string GenerateSHA256Hash(params string[] input)
    {
        var inputString = string.Join("", input);

        using (var sha256 = SHA256.Create())
        {
            var hashBytes = sha256.ComputeHash(Encoding.UTF8.GetBytes(inputString));
            return BitConverter.ToString(hashBytes).Replace("-", "").ToLowerInvariant();
        }
    }

    private static List<string> GetCsFiles(string folderPath)
    {
        return Directory.EnumerateFiles(folderPath, "*.cs", SearchOption.AllDirectories).ToList();
    }

    private static void ShowHelp()
    {
        Log("Usage: Preprocessing <input_path> <output_path>",
            "  input_path: A directory containing C# files to process.",
            "  output_path: A directory where the output graphs will be written.");
    }

    private static void Log(params string[] messages)
    {
        lock (_logLock)
        {
            foreach (var message in messages)
            {
                Console.WriteLine(message);
            }
        }
    }
}
