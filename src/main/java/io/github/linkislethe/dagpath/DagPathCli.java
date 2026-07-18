package io.github.linkislethe.dagpath;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Command-line entry point for parsing a graph and writing its maximum path. */
public final class DagPathCli {
    private DagPathCli() {
    }

    public static void main(String[] args) {
        int exitCode = run(args, System.out, System.err);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    static int run(String[] args, PrintStream output, PrintStream error) {
        if (args.length < 1 || args.length > 2) {
            error.println("Usage: java -jar dag-maximum-weight-path.jar <input> [output]");
            return 2;
        }

        Path input = Paths.get(args[0]);
        Path outputPath = args.length == 2 ? Paths.get(args[1]) : Paths.get(args[0] + "_out");
        try {
            GraphInstance graph = GraphParser.parse(input);
            PathResult result = new MaximumPathSolver().solve(graph);
            Files.write(outputPath, format(result).getBytes(StandardCharsets.UTF_8));
            output.println("Wrote result to " + outputPath);
            return 0;
        } catch (IOException | IllegalArgumentException | ArithmeticException exception) {
            error.println("Error: " + exception.getMessage());
            return 1;
        }
    }

    private static String format(PathResult result) {
        if (!result.isReachable()) {
            return "0" + System.lineSeparator() + "No path" + System.lineSeparator();
        }

        StringBuilder text = new StringBuilder();
        text.append(result.weight()).append(System.lineSeparator());
        for (int index = 0; index < result.vertices().size(); index++) {
            if (index > 0) {
                text.append(' ');
            }
            text.append(result.vertices().get(index));
        }
        return text.append(System.lineSeparator()).toString();
    }
}
