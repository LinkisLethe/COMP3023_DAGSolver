package io.github.linkislethe.dagpath;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class GraphParserTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void acceptsGeneralWhitespace() throws Exception {
        Path input = temporaryDirectory.resolve("graph.in");
        Files.write(input, Arrays.asList(
                "4",
                "0\t3",
                "3  2 1 4",
                "0 1 1 1",
                "0 0 0 1",
                "0 0 0 1",
                "0 0 0 0"), StandardCharsets.UTF_8);

        GraphInstance graph = GraphParser.parse(input);
        PathResult result = new MaximumPathSolver().solve(graph);

        assertEquals(9L, result.weight());
        assertEquals(Arrays.asList(0, 1, 3), result.vertices());
    }

    @Test
    void rejectsNonBinaryAdjacencyValues() throws Exception {
        Path input = temporaryDirectory.resolve("invalid.in");
        Files.write(input, Arrays.asList("1", "0 0", "5", "2"), StandardCharsets.UTF_8);

        assertThrows(IllegalArgumentException.class, () -> GraphParser.parse(input));
    }
}
