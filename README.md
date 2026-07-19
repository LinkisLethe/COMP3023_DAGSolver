# DAG maximum-weight path

[English](README.md) | [简体中文](README.zh-CN.md)

[![Build](https://img.shields.io/github/actions/workflow/status/LinkisLethe/COMP3023_DAGSolver/ci.yml?branch=main&style=flat-square&label=build)](https://github.com/LinkisLethe/COMP3023_DAGSolver/actions/workflows/ci.yml)
![Java 8+](https://img.shields.io/badge/Java-8%2B-007396?style=flat-square&logo=openjdk&logoColor=white)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow?style=flat-square)](LICENSE)

This repository contains a Java command-line solver for maximum-weight paths in vertex-weighted directed acyclic graphs. It reads an adjacency matrix, validates that the graph is acyclic, and writes the best path between a requested pair of vertices.

The current implementation keeps the original text input format while replacing the matrix-based solver with adjacency lists, Kahn's topological ordering, and dynamic programming. Vertex weights may be positive or negative.

## Quick start

Requirements: Java 8 or newer. The Maven Wrapper downloads the required Maven version on first use.

Windows:

```powershell
.\mvnw.cmd clean verify
java -jar target\dag-maximum-weight-path-1.0.0.jar examples\sample.in
Get-Content examples\sample.in_out
```

Linux or macOS:

```bash
./mvnw clean verify
java -jar target/dag-maximum-weight-path-1.0.0.jar examples/sample.in
cat examples/sample.in_out
```

The sample result should match [`examples/sample.expected`](examples/sample.expected).

## Input and output

The input file contains:

1. the number of vertices `n`;
2. the start and end vertex indices;
3. `n` vertex weights;
4. an `n × n` adjacency matrix containing only `0` and `1`.

Vertices are numbered from `0`. Whitespace may be spaces, tabs, or line breaks. A second CLI argument can specify the output path; otherwise the program appends `_out` to the input path.

For a reachable destination, the output contains the maximum total vertex weight and the selected path:

```text
9
0 1 3
```

An unreachable destination produces:

```text
0
No path
```

Cycles, malformed matrices, invalid vertex indices, extra input values, and path-weight overflow are reported as errors.

## Algorithm and complexity

The parser converts the matrix to adjacency lists. The solver then computes indegrees, performs Kahn's topological ordering, and relaxes outgoing edges in that order. A predecessor array reconstructs the selected path.

| Stage | Time | Extra space |
|---|---:|---:|
| Parse the matrix | `O(V²)` | `O(V + E)` |
| Topological ordering and path DP | `O(V + E)` | `O(V)` |

The matrix input format makes total parsing time `O(V²)`, even though the solver itself is linear in the stored graph. Equal-weight alternatives are resolved deterministically by input vertex order; the selected path is not promised to be lexicographically smallest.

## Verification

`mvnw verify` runs eight JUnit checks. They cover the supplied sample, `start == end`, unreachable destinations, equal-weight alternatives, cycle rejection, flexible whitespace, invalid matrix values, and 200 seeded random DAGs checked against exhaustive path enumeration.

## Repository layout

```text
examples/   Sample input and expected output
legacy/     De-identified course implementation
src/main/   Parser, graph model, solver, and CLI
src/test/   Unit and randomized regression tests
```

## Provenance

The solver began as a COMP3023 Design and Analysis of Algorithms exercise. The `legacy/` directory preserves the de-identified course implementation; the Maven structure, input validation, adjacency-list solver, tests, CI, and documentation are later maintenance recorded in Git history.

## License

[MIT](LICENSE)
