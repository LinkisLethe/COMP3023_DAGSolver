package io.github.linkislethe.dagpath;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

/** Finds a maximum-weight path between two vertices in a directed acyclic graph. */
public final class MaximumPathSolver {
    public PathResult solve(GraphInstance graph) {
        List<Integer> topologicalOrder = topologicalOrder(graph);
        long[] bestWeight = new long[graph.size()];
        boolean[] reachable = new boolean[graph.size()];
        int[] predecessor = new int[graph.size()];
        Arrays.fill(predecessor, -1);

        reachable[graph.start()] = true;
        bestWeight[graph.start()] = graph.weight(graph.start());

        for (int from : topologicalOrder) {
            if (!reachable[from]) {
                continue;
            }
            for (int to : graph.neighbors(from)) {
                long candidate = Math.addExact(bestWeight[from], graph.weight(to));
                if (!reachable[to] || candidate > bestWeight[to]) {
                    reachable[to] = true;
                    bestWeight[to] = candidate;
                    predecessor[to] = from;
                }
            }
        }

        if (!reachable[graph.end()]) {
            return PathResult.unreachable();
        }

        List<Integer> path = new ArrayList<Integer>();
        for (int vertex = graph.end(); vertex != -1; vertex = predecessor[vertex]) {
            path.add(vertex);
        }
        Collections.reverse(path);
        return PathResult.reachable(bestWeight[graph.end()], path);
    }

    private List<Integer> topologicalOrder(GraphInstance graph) {
        int[] indegree = new int[graph.size()];
        for (int from = 0; from < graph.size(); from++) {
            for (int to : graph.neighbors(from)) {
                indegree[to]++;
            }
        }

        Deque<Integer> ready = new ArrayDeque<Integer>();
        for (int vertex = 0; vertex < graph.size(); vertex++) {
            if (indegree[vertex] == 0) {
                ready.addLast(vertex);
            }
        }

        List<Integer> order = new ArrayList<Integer>(graph.size());
        while (!ready.isEmpty()) {
            int from = ready.removeFirst();
            order.add(from);
            for (int to : graph.neighbors(from)) {
                indegree[to]--;
                if (indegree[to] == 0) {
                    ready.addLast(to);
                }
            }
        }

        if (order.size() != graph.size()) {
            throw new IllegalArgumentException("Input graph contains a cycle");
        }
        return order;
    }
}
