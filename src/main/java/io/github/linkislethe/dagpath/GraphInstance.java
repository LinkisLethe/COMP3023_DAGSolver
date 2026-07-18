package io.github.linkislethe.dagpath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Immutable vertex-weighted directed graph and a requested start/end pair. */
public final class GraphInstance {
    private final long[] weights;
    private final List<List<Integer>> adjacency;
    private final int start;
    private final int end;

    public GraphInstance(long[] weights, List<List<Integer>> adjacency, int start, int end) {
        if (weights == null || adjacency == null || weights.length == 0) {
            throw new IllegalArgumentException("The graph must contain at least one vertex");
        }
        if (weights.length != adjacency.size()) {
            throw new IllegalArgumentException("Weights and adjacency sizes do not match");
        }
        if (start < 0 || start >= weights.length || end < 0 || end >= weights.length) {
            throw new IllegalArgumentException("Start and end vertices must be within the graph");
        }

        this.weights = Arrays.copyOf(weights, weights.length);
        List<List<Integer>> copiedAdjacency = new ArrayList<List<Integer>>(adjacency.size());
        for (List<Integer> neighbors : adjacency) {
            if (neighbors == null) {
                throw new IllegalArgumentException("Adjacency lists cannot be null");
            }
            List<Integer> copy = new ArrayList<Integer>(neighbors.size());
            for (Integer neighbor : neighbors) {
                if (neighbor == null || neighbor < 0 || neighbor >= weights.length) {
                    throw new IllegalArgumentException("An edge references an unknown vertex");
                }
                copy.add(neighbor);
            }
            copiedAdjacency.add(Collections.unmodifiableList(copy));
        }
        this.adjacency = Collections.unmodifiableList(copiedAdjacency);
        this.start = start;
        this.end = end;
    }

    public int size() {
        return weights.length;
    }

    public long weight(int vertex) {
        return weights[vertex];
    }

    public List<Integer> neighbors(int vertex) {
        return adjacency.get(vertex);
    }

    public int start() {
        return start;
    }

    public int end() {
        return end;
    }
}
