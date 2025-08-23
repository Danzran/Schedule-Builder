
import java.util.Scanner;

class DisjointSet {
    private int[] parent;
    private int[] rank;
    private int numTrees;

    public DisjointSet(int n) {
        parent = new int[n];
        rank = new int[n];
        numTrees = n;
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            rank[i] = 0;
        }
    }

    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }

    public void union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);
        if (rootX != rootY) {
            if (rank[rootX] > rank[rootY]) {
                parent[rootY] = rootX;
            } else if (rank[rootX] < rank[rootY]) {
                parent[rootX] = rootY;
            } else {
                parent[rootY] = rootX;
                rank[rootX]++;
            }
            numTrees--;
        }
    }

    public int getNumTrees() {
        return numTrees;
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int N = scanner.nextInt();
        int M = scanner.nextInt();

        DisjointSet disjointSet = new DisjointSet(N);
        int[][] edges = new int[M][2];

        for (int i = 0; i < M; i++) {
            edges[i][0] = scanner.nextInt() - 1;
            edges[i][1] = scanner.nextInt() - 1;
        }

        int[] order = new int[N];

        for (int i = N - 1; i >= 0; i--) {
            order[i] = scanner.nextInt() - 1;
        }

        scanner.close();

        boolean[] isConnected = new boolean[N];
        int[] result = new int[N];

        for (int i = 0; i < N; i++) {
            isConnected[i] = true;

            for (int j = 0; j < M; j++) {
                int u = edges[j][0];
                int v = edges[j][1];

                if ((u != order[i] && isConnected[u]) || (v != order[i] && isConnected[v])) {
                    disjointSet.union(u, v);
                }
            }

            result[i] = (disjointSet.getNumTrees() == 1) ? 1 : 0;
        }

        for (int i = N - 1; i >= 0; i--) {
            System.out.println((result[i] == 1) ? "YES" : "NO");
        }
    }
}