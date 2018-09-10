package cn.cstn.algorithm.commons;

/**
 * description :        two tuple
 * @author :            zhaohq
 * date :               2018/8/31 0031 17:23
 */
public class Tuple<T> extends KV<T, T> {
    private T[] a;

    @SafeVarargs
    public Tuple(T _1, T _2, T... a) {
        super(_1, _2);
        this.a = a;
    }

    public T g_(int i) {
        if (i == 0) return _1();
        else if (i == 1) return _2();
        return a[i - 2];
    }

    public void s_(int i, T t) {
        if (i == 0) this.s_1(t);
        else if (i == 1) this.s_2(t);
        else a[i - 2] = t;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("(" + _1() + ", " + _2());
        for (T t: a)
            s.append(", ").append(t);
        s.append(")");
        return s.toString();
    }

}
