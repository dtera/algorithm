package cn.cstn.algorithm.commons;

/**
 * description :        two tuple
 * @author :            zhaohq
 * date :               2018/8/31 0031 17:23
 */
public class Tuple<T> {
    private T _1;
    private T _2;

    public Tuple(T _1, T _2) {
        this._1 = _1;
        this._2 = _2;
    }

    public T _1() {
        return _1;
    }

    public T _2() {
        return _2;
    }

    public void s_1(T _1) {
        this._1 = _1;
    }

    public void s_2(T _2) {
        this._2 = _2;
    }

    @Override
    public String toString() {
        return "(" + _1 + ", " + _2 + ")";
    }

}
