package cn.cstn.algorithm.commons;

/**
 * description :        key value
 * @author :            zhaohq
 * date :               2018/8/31 0031 17:23
 */
public class KV<K, V> {
    private K _1;
    private V _2;

    public KV(K _1, V _2) {
        this._1 = _1;
        this._2 = _2;
    }

    public K _1() {
        return _1;
    }

    public V _2() {
        return _2;
    }

    public void s_1(K _1) {
        this._1 = _1;
    }

    public void s_2(V _2) {
        this._2 = _2;
    }

    @Override
    public String toString() {
        return "(" + _1 + ", " + _2 + ")";
    }

}
