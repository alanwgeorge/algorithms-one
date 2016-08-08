package com.alangeorge.algorithums.one;

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class UF {
    private int[] id;
    private int[] depth;

    public UF(int n) {
        id = new int[n];
        depth = new int[n];
        for (int i = 0; i < n; i++) {
            id[i] = i;
            depth[i] = 1;
        }
    }

    public void unionSlow(int p, int q) {
        int idp = id[p];
        int idq = id[q];
        for (int i = 0; i < id.length; i++) {
            if (id[i] == idp) id[i] = idq;
        }
    }

    public void unionFast(int p, int q) {
        int rootP = rootI(p);
        int rootQ = rootI(q);

        if (depth[rootP] >= depth[rootQ]) {
            id[rootQ] = rootP;
            depth[rootP] = depth[rootP] + depth[rootQ];
        } else {
            id[rootP] = rootQ;
            depth[rootQ] = depth[rootQ] + depth[rootP];
        }
    }

    public boolean connectedFast(int p, int q) {
        return id[p] == id[q];
    }

    public boolean connectedSlow(int p, int q) {
        return rootI(p) == rootI(q);
    }

    public int rootR(int node) {
        if (node == id[node]) {
            return node;
        } else {
            return rootR(id[node]);
        }
    }

    public int rootI(int node) {
        int parent = id[node];

        while (parent != id[parent]) {
            id[parent] = id[id[parent]];
            parent = id[parent];
        }

        return parent;
    }

    @Override
    public String toString() {
        return Arrays.toString(id) + "\n" + Arrays.toString(depth);
    }

    public static void main(String[] args) {
        int N = StdIn.readInt();
        UF uf = new UF(N);

        while (!StdIn.isEmpty()) {
            int p = StdIn.readInt();
            int q = StdIn.readInt();

            StdOut.println(p + " " + q);

            if (!uf.connectedSlow(p, q)) {
                uf.unionFast(p, q);
            } else {
                StdOut.println(p + " " + q + " already connected");
            }
        }
//        StdOut.println(uf);
    }
}
