package cn.cstn.algorithm.commons.graph;

import cn.cstn.algorithm.commons.UF;

/**
 * description :        matrix union find
 * @author :           zhaohq
 * date :               2018/8/26 0026 14:46
 */
public class MUF extends UF {
    private int[][] a;
    private int[][] connectedGraph;
    private boolean hasSetCG;

    public MUF(int[][] a) {
        super(a.length * a[0].length);
        this.a = a;
        connectedGraph = new int[a.length][a[0].length];
    }

    public boolean isConnected(int i, int j, int p, int q) {
        return super.isConnected(a[0].length * i + j, a[0].length * p + q);
    }

    public int find(int i, int j) {
        return super.find(a[0].length * i + j);
    }

    public void union(int i, int j, int p, int q) {
        super.union(a[0].length * i + j, a[0].length * p + q);
    }

    public void decNumOfComponent() {
        this.numOfComponent--;
    }

    @Override
    protected boolean shouldAddToGroup(int k) {
        int n = a[0].length, i = k / n, j = k % n;
        return shouldAddToGroup(i, j);
    }

    private boolean shouldAddToGroup(int i, int j) {
        return a[i][j] != 0;
    }

    public void buildConnectedComponent() {
        int m = a.length, n = a[0].length;
        int[][] d = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (a[i][j] == 0) {
                    decNumOfComponent();
                    continue;
                }
                for (int[] dk : d) {
                    int p = i + dk[0];
                    int q = j + dk[1];
                    if (p < 0 || p >= m || q < 0 || q >= n || a[p][q] == 0) continue;
                    union(i, j, p, q);
                }
            }
        }

        super.buildConnectedComponent(null);
    }

    public int[][] getA() {
        return a;
    }

    public int[][] getConnectedGraph() {
        if (!hasSetCG)
            for (int i = 0; i < connectedGraph.length; i++)
                for (int j = 0; j < connectedGraph[0].length; j++) {
                    connectedGraph[i][j] = find(i, j);
                    if (a[i][j] == 0) connectedGraph[i][j] = -1;
                }

        hasSetCG = true;
        return connectedGraph;
    }

}
