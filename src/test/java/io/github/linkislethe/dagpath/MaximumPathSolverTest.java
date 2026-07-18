package io.github.linkislethe.dagpath;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

class MaximumPathSolverTest {
    private final MaximumPathSolver solver = new MaximumPathSolver();

    @Test
    void solvesProvidedSample() {
        GraphInstance graph = graph(
                new long[] {3, 2, 1, 4},
                new int[][] {{1, 2, 3}, {3}, {3}, {}},
                0,
                3);

        PathResult result = solver.solve(graph);

        assertTrue(result.isReachable());
        assertEquals(9L, result.weight());
        assertEquals(Arrays.asList(0, 1, 3), result.vertices());
    }

    @Test
    void returnsStartVertexWhenStartEqualsEnd() {
        GraphInstance graph = graph(new long[] {-7}, new int[][] {{}}, 0, 0);

        PathResult result = solver.solve(graph);

        assertTrue(result.isReachable());
        assertEquals(-7L, result.weight());
        assertEquals(Collections.singletonList(0), result.vertices());
    }

    @Test
    void reportsUnreachableDestination() {
        GraphInstance graph = graph(new long[] {2, 4, 6}, new int[][] {{1}, {}, {}}, 0, 2);

        PathResult result = solver.solve(graph);

        assertFalse(result.isReachable());
        assertTrue(result.vertices().isEmpty());
    }

    @Test
    void resolvesEqualPathsDeterministically() {
        GraphInstance graph = graph(
                new long[] {1, 2, 2, 3},
                new int[][] {{1, 2}, {3}, {3}, {}},
                0,
                3);

        PathResult result = solver.solve(graph);

        assertEquals(6L, result.weight());
        assertEquals(Arrays.asList(0, 1, 3), result.vertices());
    }

    @Test
    void rejectsCycles() {
        GraphInstance graph = graph(new long[] {1, 2}, new int[][] {{1}, {0}}, 0, 1);

        assertThrows(IllegalArgumentException.class, () -> solver.solve(graph));
    }

    @Test
    void agreesWithBruteForceOnRandomSmallDags() {
        Random random = new Random(3023L);
        for (int trial = 0; trial < 200; trial++) {
            int size = 2 + random.nextInt(7);
            long[] weights = new long[size];
            int[][] edges = new int[size][];
            for (int vertex = 0; vertex < size; vertex++) {
                weights[vertex] = random.nextInt(16) - 5;
                List<Integer> neighbors = new ArrayList<Integer>();
                for (int to = vertex + 1; to < size; to++) {
                    if (random.nextDouble() < 0.35) {
                        neighbors.add(to);
                    }
                }
                edges[vertex] = neighbors.stream().mapToInt(Integer::intValue).toArray();
            }

            GraphInstance graph = graph(weights, edges, 0, size - 1);
            PathResult actual = solver.solve(graph);
            Long expected = bruteForce(graph, 0, 0L);

            assertEquals(expected != null, actual.isReachable(), "trial " + trial);
            if (expected != null) {
                assertEquals(expected.longValue(), actual.weight(), "trial " + trial);
            }
        }
    }

    private Long bruteForce(GraphInstance graph, int vertex, long accumulatedBeforeVertex) {
        long accumulated = accumulatedBeforeVertex + graph.weight(vertex);
        if (vertex == graph.end()) {
            return accumulated;
        }

        Long best = null;
        for (int next : graph.neighbors(vertex)) {
            Long candidate = bruteForce(graph, next, accumulated);
            if (candidate != null && (best == null || candidate > best)) {
                best = candidate;
            }
        }
        return best;
    }

    private GraphInstance graph(long[] weights, int[][] edges, int start, int end) {
        List<List<Integer>> adjacency = new ArrayList<List<Integer>>(edges.length);
        for (int[] outgoing : edges) {
            List<Integer> neighbors = new ArrayList<Integer>(outgoing.length);
            for (int vertex : outgoing) {
                neighbors.add(vertex);
            }
            adjacency.add(neighbors);
        }
        return new GraphInstance(weights, adjacency, start, end);
    }
}
