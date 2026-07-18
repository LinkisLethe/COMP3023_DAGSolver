package io.github.linkislethe.dagpath;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Parses the adjacency-matrix format used by the command-line application. */
public final class GraphParser {
    private GraphParser() {
    }

    public static GraphInstance parse(Path input) throws IOException {
        try (InputStream stream = new BufferedInputStream(Files.newInputStream(input))) {
            TokenReader reader = new TokenReader(stream);
            int size = readInt(reader, "vertex count");
            if (size <= 0) {
                throw new IllegalArgumentException("Vertex count must be positive");
            }

            int start = readInt(reader, "start vertex");
            int end = readInt(reader, "end vertex");
            long[] weights = new long[size];
            for (int vertex = 0; vertex < size; vertex++) {
                weights[vertex] = readLong(reader, "weight for vertex " + vertex);
            }

            List<List<Integer>> adjacency = new ArrayList<List<Integer>>(size);
            for (int from = 0; from < size; from++) {
                List<Integer> neighbors = new ArrayList<Integer>();
                for (int to = 0; to < size; to++) {
                    int value = readInt(reader, "adjacency entry " + from + "," + to);
                    if (value != 0 && value != 1) {
                        throw new IllegalArgumentException("Adjacency entries must be 0 or 1");
                    }
                    if (value == 1) {
                        neighbors.add(to);
                    }
                }
                adjacency.add(neighbors);
            }

            if (reader.next() != null) {
                throw new IllegalArgumentException("Input contains extra values");
            }
            return new GraphInstance(weights, adjacency, start, end);
        }
    }

    private static int readInt(TokenReader reader, String label) throws IOException {
        long value = readLong(reader, label);
        if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(label + " is outside the integer range");
        }
        return (int) value;
    }

    private static long readLong(TokenReader reader, String label) throws IOException {
        String token = reader.next();
        if (token == null) {
            throw new IllegalArgumentException("Missing " + label);
        }
        try {
            return Long.parseLong(token);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Invalid " + label + ": " + token, exception);
        }
    }

    private static final class TokenReader {
        private final InputStream input;

        private TokenReader(InputStream input) {
            this.input = input;
        }

        private String next() throws IOException {
            StringBuilder token = new StringBuilder();
            int current;
            do {
                current = input.read();
            } while (current != -1 && Character.isWhitespace((char) current));

            while (current != -1 && !Character.isWhitespace((char) current)) {
                token.append((char) current);
                current = input.read();
            }
            return token.length() == 0 ? null : token.toString();
        }
    }
}
