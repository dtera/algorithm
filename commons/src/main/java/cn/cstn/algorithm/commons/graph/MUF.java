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

}
