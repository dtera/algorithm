package cn.cstn.algorithm.commons;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * description :        union find
 * @author :            zhaohq
 * date :               2018/8/12 0012 18:16
 */
public class UF {
    private int[] id;
    private int[] sz;
    protected int numOfComponent;
    private int[] nums;

    public UF(int n) {
        id = new int[n];
        sz = new int[n];
        for (int i = 0; i < n; i++) {
            id[i] = i;
            sz[i] = 1;
        }
        numOfComponent = n;
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
        if (sz[pr] <= sz[qr]) {
            id[pr] = qr;
            sz[qr] += sz[pr];
        } else {
            id[qr] = pr;
            sz[pr] += sz[qr];
        }
        numOfComponent--;
    }

    public int getNumOfComponent() {
        return numOfComponent;
    }

    public int[] getNums() {
        return nums;
    }

    private void setNums() {
        nums = new int[numOfComponent];
        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < sz.length; i++)
            if (shouldAddToGroup(i))
                set.add(find(i));

        assert numOfComponent == set.size();
        int k = 0;
        for (Integer i: set)
            nums[k++] = sz[i];
    }

    protected boolean shouldAddToGroup(int i) {
        return i >= 0 && i < id.length;
    }

    public void buildConnectedComponent(Consumer<UF> consumer) {
        if (consumer != null)
            consumer.accept(this);
        setNums();
    }

}
