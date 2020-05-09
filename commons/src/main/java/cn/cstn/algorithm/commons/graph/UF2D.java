package cn.cstn.algorithm.commons.graph;

import cn.cstn.algorithm.commons.UF;
import cn.cstn.algorithm.commons.util.ArrayUtil;
import lombok.Getter;

/**
 * description :        union find for 2 dimension array
 *
 * @author :            zhaohq
 * @date :               2018/8/26 0026 14:46
 */
public class UF2D extends UF {
    @Getter
    private final int[][] a;
    private final int[][] connectedGraph;
    private boolean hasSetCG;

    public UF2D(int[][] a) {
        super(a.length * a[0].length);
        this.a = a;
        connectedGraph = new int[a.length][a[0].length];
    }

    public boolean isConnected(int i, int j, int p, int q) {
        return super.isConnected(_2_1(i, j), _2_1(p, q));
    }

    public int find(int i, int j) {
        return super.find(_2_1(i, j));
    }

    public void union(int i, int j, int p, int q) {
        super.union(_2_1(i, j), _2_1(p, q));
    }

    private int _2_1(int i, int j) {
        return a[0].length * i + j;
    }

    /*private Pair<Integer, Integer> _1_2(int k) {
        int n = a[0].length, i = k / n, j = k % n;
        return Pair.of(i, j);
    }*/

    public void decNumOfComponent() {
        this.numOfComponent--;
    }

    public void buildConnectedComponent() {
        int m = a.length, n = a[0].length;
        int[][] d = ArrayUtil.getWalkDirections();

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (a[i][j] == 0) {
                    decNumOfComponent();
                    this.roots.remove(_2_1(i, j));
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
