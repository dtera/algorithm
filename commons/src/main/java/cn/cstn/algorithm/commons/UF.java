package cn.cstn.algorithm.commons;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * description :        union find
 *
 * @author :            zhaohq
 * date :               2018/8/12 0012 18:16
 */
public class UF {
    private final int[] id;
    private final int[] sz;
    @Getter
    protected int numOfComponent;
    @Getter
    protected final Set<Integer> roots;

    public UF(int n) {
        id = new int[n];
        sz = new int[n];
        numOfComponent = n;
        roots = new HashSet<>();
        for (int i = 0; i < n; i++) {
            id[i] = i;
            sz[i] = 1;
            roots.add(i);
        }
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
            roots.remove(pr);
        } else {
            id[qr] = pr;
            sz[pr] += sz[qr];
            roots.remove(qr);
        }
        numOfComponent--;
    }

    public Integer[] getSizeOfComponents() {
        return roots.stream().map(i -> sz[i]).toArray(Integer[]::new);
    }

    public void buildConnectedComponent(Consumer<UF> consumer) {
        if (consumer != null)
            consumer.accept(this);
    }

}
