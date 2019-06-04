package cn.cstn.algorithm.commons.util;


import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Consumer;

/**
 * description :        CollectionUtil
 *
 * @author :            zhaohq
 * date :               2018/8/28 0028 20:39
 */
public class CollectionUtil {

    public static <T> void combinationPair(List<T> pls, Consumer<Pair<T, T>> consumer) {
        for (int i = 0; i < pls.size() - 1; i++) {
            T pl = pls.get(i);
            for (int j = i + 1; j < pls.size(); j++) {
                T cl = pls.get(j);
                consumer.accept(Pair.of(pl, cl));
            }
        }
    }

    public static <T> void combinationPair(List<T> pls, List<T> cls, Consumer<Pair<T, T>> consumer) {
        for (T pl : pls) {
            for (T cl : cls) {
                consumer.accept(Pair.of(pl, cl));
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

    public static List<Pair<Integer, Integer>> _merge(List<Pair<Integer, Integer>> ts) {
        List<Pair<Integer, Integer>> res = new ArrayList<>();
        if (ts == null || ts.size() < 1) return res;
        int n = ts.size();
        int[] ss = new int[n], es = new int[n];
        for (int i = 0; i < n; i++) {
            ss[i] = ts.get(i).getLeft();
            es[i] = ts.get(i).getRight();
        }

        Arrays.sort(ss);
        Arrays.sort(es);

        for (int i = 0, j = 0; i < n; i++)
            if (i == n - 1 || ss[i + 1] > es[i]) {
                res.add(Pair.of(ss[j], es[i]));
                j = i + 1;
            }

        return res;
    }

    public static List<Pair<Integer, Integer>> merge(List<Pair<Integer, Integer>> ts) {
        List<Pair<Integer, Integer>> res = new ArrayList<>();
        if (ts == null || ts.size() < 1) return res;
        ts.sort(Comparator.comparingInt(Pair::getLeft));
        //method1
        for (int i = 0, j = 0; i < ts.size(); i++)
            if (i == ts.size() - 1 || ts.get(i + 1).getLeft() > ts.get(i).getRight()) {
                res.add(Pair.of(ts.get(j).getLeft(), ts.get(i).getRight()));
                j = i + 1;
            }
        //method2
        /*Pair p = ts.get(0);
        int s = p.getLeft(), e = p.getRight();
        for (int i = 1; i < ts.size(); i++) {
            p = ts.get(i);
            if (p.getLeft() > e) {
                res.add(new Pair(s, e));
                s = p.getLeft();
                e = p.getRight();
            } else e = Math.max(p.getRight(), e);
        }
        res.add(new Pair(s, e));*/
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
