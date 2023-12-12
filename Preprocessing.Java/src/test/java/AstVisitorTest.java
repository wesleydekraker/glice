import org.example.GraphGenerator;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AstVisitorTest {
    @Test
    public void testSomething() throws URISyntaxException, IOException {
        URL url = AstVisitorTest.class.getClassLoader().getResource("Example.java");
        assert url != null;
        Path path = Paths.get(url.toURI());
        var graphs = GraphGenerator.generate(path);

        for (var node : graphs.get(0).nodes()) {
            System.out.println(node.id() + ", " + node.nodeType() + ", " + node.value());
        }

        System.out.println("Ast");
        for (var node : graphs.get(0).astEdges()) {
            System.out.println(node.from() + ", " + node.to());
        }

        System.out.println("Cfg");
        for (var node : graphs.get(0).cfgEdges()) {
            System.out.println(node.from() + ", " + node.to());
        }

        System.out.println("Cdg");
        for (var node : graphs.get(0).cdgEdges()) {
            System.out.println(node.from() + ", " + node.to());
        }

        System.out.println("ReachingDef");
        for (var node : graphs.get(0).reachingDefEdges()) {
            System.out.println(node.from() + ", " + node.to());
        }
    }
}