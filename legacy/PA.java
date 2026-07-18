// Original course implementation. Personal identifiers removed.

//package
import java.io.*;
import java.util.*;

public class PA {

    static int n, start, end;
    static int[] pred;
    static boolean[] v; //visited
    static int[] weights;
    static int[] maxSum;
    static int[][] adj;
    static List<Integer> oNodes = new ArrayList<>();  //ordered nodes list

    public static void main(String[] args) throws IOException {
        long startTime = System.nanoTime();
        System.out.println("The file name is \"" + args[0] + "\".");
        String input = args[0];
        Scanner s = new Scanner(new File(input));

        //read file
        n = Integer.parseInt(s.nextLine());

        //load start and end vertex
        String[] v1 = s.nextLine().split(" ");
        start = Integer.parseInt(v1[0]);
        end = Integer.parseInt(v1[1]);

        //read weight
        String[] w = s.nextLine().split(" ");
        weights = new int[n];
        for (int i = 0; i < n; i++) {
            weights[i] = Integer.parseInt(w[i]);
        }

        //build the matrix
        adj = new int[n][n];
        for (int i = 0; i < n; i++) {
            String[] r = s.nextLine().split(" ");
            for (int j = 0; j < n; j++) {
                adj[i][j] = Integer.parseInt(r[j]);
            }
        }

        // special case: start = end
        if (start == end) {
            String output = input + "_out";
            PrintWriter o = new PrintWriter(new FileWriter(output));
            o.println(weights[start]);
            o.println(start);
            o.close();
            return;
        }

        //DFS + max path sum
        DFSprocess();

        //write output
        String output = input + "_out";
        PrintWriter o = new PrintWriter(new FileWriter(output)); //output object

        int minValue = -999999999;
        if (maxSum[end] == minValue) {
            o.println("0");
            o.println("No path");
            o.close();
            return;
        }

        o.println(maxSum[end]); //output max sum

        List<Integer> path = new ArrayList<>();
        for (int curr = end; curr != -1; curr = pred[curr]) {
            path.add(curr);
        }

        //reverse path
        for (int i = path.size() - 1; i >= 0; i--) {
            o.print(path.get(i));
            if (i > 0) {
                o.print(" ");
            }
        }
        o.println();
        o.close();
        long endTime = System.nanoTime();
        double diff = (endTime - startTime) / 1e6;
        System.out.printf("Execution time：%.3f ms%n", diff);
    }

    //DFS + DP for max path sum
    static void DFSprocess() {
        int minValue = -999999999; //set as min value
        v = new boolean[n];
        for (int i = 0; i < n; i++) {
            if (!v[i]) {
                DFSVisit(i);
            }
        }

        //reverse oNodes
        List<Integer> reversed = new ArrayList<>();
        for (int i = oNodes.size() - 1; i >= 0; i--) {
            reversed.add(oNodes.get(i));
        }
        oNodes = reversed;

        //initialize
        maxSum = new int[n];
        pred = new int[n];
        for (int i = 0; i < n; i++) {
            maxSum[i] = minValue;
            pred[i] = -1;
        }

        maxSum[start] = weights[start];

        //dynamic programming
        for (int u : oNodes) {
            if (maxSum[u] == minValue) {
                continue;
            }
            for (int vertex = 0; vertex < n; vertex++) {
                if (adj[u][vertex] == 1) {
                    int temp = maxSum[u] + weights[vertex];
                    if (temp > maxSum[vertex]) {
                        maxSum[vertex] = temp;
                        pred[vertex] = u;
                    }
                }
            }
        }
    }

    //recursive DFSVisit
    static void DFSVisit(int u) {
        v[u] = true;
        for (int vertex = 0; vertex < n; vertex++) {
            if (adj[u][vertex] == 1 && !v[vertex]) {
                DFSVisit(vertex);
            }
        }
        oNodes.add(u);
    }
}
