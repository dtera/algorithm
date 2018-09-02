package cn.cstn.algorithm.commons.util;

import cn.cstn.algorithm.commons.Tuple;

import java.util.*;
import java.util.function.Consumer;

/**
 * description :        CollectionUtil
 * @author :            zhaohq
 * date :               2018/8/28 0028 20:39
 */
public class CollectionUtil {

    public static <T> void combinationPair(List<T> pls, Consumer<Tuple<T>> consumer) {
        for (int i = 0; i < pls.size() - 1; i++) {
            T pl = pls.get(i);
            for (int j = i + 1; j < pls.size(); j++) {
                T cl = pls.get(j);
                consumer.accept(new Tuple<>(pl, cl));
            }
        }
    }

    public static <T> void combinationPair(List<T> pls, List<T> cls, Consumer<Tuple<T>> consumer) {
        for (T pl : pls) {
            for (T cl : cls) {
                consumer.accept(new Tuple<>(pl, cl));
            }
        }
    }

    public static <T> List<T> intersect(List<T> ls1, List<T> ls2) {
        List<T> list = new ArrayList<>(ls1);
        list.retainAll(ls2);
        return list;
    }

    public static <T> List<T> union(List<T> ls1, List<T> ls2) {
        List<T> list = new ArrayList<>(ls1);
        list.addAll(ls2);
        return list;
    }

    public static <T> List<T> diff(List<T> ls1, List<T> ls2) {
        List<T> list = new ArrayList<>(ls1);
        list.removeAll(ls2);
        return list;
    }

    public static List<Tuple<Integer>> _merge(List<Tuple<Integer>> ts) {
        List<Tuple<Integer>> res = new ArrayList<>();
        if (ts == null || ts.size() < 1) return res;
        int n = ts.size();
        int[] ss = new int[n], es = new int[n];
        for (int i = 0; i < n; i++) {
            ss[i] = ts.get(i)._1();
            es[i] = ts.get(i)._2();
        }

        Arrays.sort(ss);
        Arrays.sort(es);

        for (int i = 0, j = 0; i < n; i++)
            if (i == n - 1 || ss[i + 1] > es[i]) {
                res.add(new Tuple<>(ss[j], es[i]));
                j = i + 1;
            }

        return res;
    }

    public static List<Tuple<Integer>> merge(List<Tuple<Integer>> ts) {
        List<Tuple<Integer>> res = new ArrayList<>();
        if (ts == null || ts.size() < 1) return res;
        ts.sort(Comparator.comparingInt(Tuple::_1));
        //method1
        for (int i = 0, j = 0; i < ts.size(); i++)
            if (i == ts.size() - 1 || ts.get(i + 1)._1() > ts.get(i)._2()) {
                res.add(new Tuple<>(ts.get(j)._1(), ts.get(i)._2()));
                j = i + 1;
            }
        //method2
        /*Tuple p = ts.get(0);
        int s = p._1(), e = p._2();
        for (int i = 1; i < ts.size(); i++) {
            p = ts.get(i);
            if (p._1() > e) {
                res.add(new Tuple(s, e));
                s = p._1();
                e = p._2();
            } else e = Math.max(p._2(), e);
        }
        res.add(new Tuple(s, e));*/
        return res;
    }

    public static <T> void println(Iterable<T> it) {
        print(it, ", ", "[", "]\n");
    }

    public static <T> void print(Iterable<T> it, String sep, String s, String e) {
        print(it, sep, s, e, System.out::print);
    }

    public static <T> void print(Iterable<T> it, String sep, String s, String e, Consumer<T> c) {
        if (it == null) return;
        Iterator<T> iterator = it.iterator();
        T last = null;

        System.out.print(s);
        while (iterator.hasNext()) {
            T pre = iterator.next();
            if (iterator.hasNext()) {
                c.accept(pre);
                System.out.print(sep);
            }
            last = pre;
        }

        if (last != null) {
            c.accept(last);
            System.out.print(e);
        }
    }

}
