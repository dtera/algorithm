package cn.cstn.algorithm.commons;

/**
 * description :        union find
 * @author :           zhaohq
 * date :               2018/8/12 0012 18:14
 */
public class UF {
    private int[] id;
    private int[] sz;
    private int count;

    public UF(int n) {
        id = new int[n];
        sz = new int[n];
        for (int i = 0; i < n; i++) {
            id[i] = i;
            sz[i] = 1;
        }
        count = n;
    }

    public boolean isConnected(int p, int q) {
        return find(p) == find(q);
    }

    public int find(int p) {
        int r = p;
        while (r != id[r])
            r = id[r];

        while (p != id[p]) {
            int q = id[p];
            id[p] = r;
            p = q;
        }

        return r;
    }

    public void union(int p, int q) {
        int pr = find(p);
        int qr = find(q);
        if (pr == qr) return;
        if (sz[pr] < sz[qr]) {
            id[pr] = qr;
            sz[qr] += sz[pr];
        } else {
            id[qr] = pr;
            sz[pr] += sz[qr];
        }
        count--;
    }

    public int getCount() {
        return count;
    }

}
