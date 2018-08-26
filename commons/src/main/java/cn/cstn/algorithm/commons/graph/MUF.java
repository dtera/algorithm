package cn.cstn.algorithm.commons.graph;

import cn.cstn.algorithm.commons.UF;

/**
 * description :        matrix union find
 * @author :           zhaohq
 * date :               2018/8/26 0026 14:46
 */
public class MUF extends UF {
    private int n;

    public MUF(int m, int n) {
        super(m * n);
        this.n = n;
    }

    public boolean isConnected(int i, int j, int p, int q) {
        return super.isConnected(n * i + j, n * p + q);
    }

    public int find(int i, int j) {
        return super.find(n * i + j);
    }

    public void union(int i, int j, int p, int q) {
        super.union(n * i + j, n * p + q);
    }

    public void deCount() {
        this.count--;
    }

    public static MUF connectedComponent(int[][] a) {
        int m = a.length, n = a[0].length;
        MUF muf = new MUF(m, n);
        int[][] d = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (a[i][j] == 0) {
                    muf.deCount();
                    continue;
                }
                for (int[] dk : d) {
                    int p = i + dk[0];
                    int q = j + dk[1];
                    if (p < 0 || p >= m || q < 0 || q >= n || a[p][q] == 0) continue;
                    muf.union(i, j, p, q);
                }
            }
        }

        return muf;
    }

}
