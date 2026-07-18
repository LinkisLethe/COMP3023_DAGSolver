package io.github.linkislethe.dagpath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Result of a maximum-weight path query. */
public final class PathResult {
    private final boolean reachable;
    private final long weight;
    private final List<Integer> vertices;

    private PathResult(boolean reachable, long weight, List<Integer> vertices) {
        this.reachable = reachable;
        this.weight = weight;
        this.vertices = Collections.unmodifiableList(new ArrayList<Integer>(vertices));
    }

    public static PathResult reachable(long weight, List<Integer> vertices) {
        return new PathResult(true, weight, vertices);
    }

    public static PathResult unreachable() {
        return new PathResult(false, 0L, Collections.<Integer>emptyList());
    }

    public boolean isReachable() {
        return reachable;
    }

    public long weight() {
        if (!reachable) {
            throw new IllegalStateException("An unreachable result has no path weight");
        }
        return weight;
    }

    public List<Integer> vertices() {
        return vertices;
    }
}
