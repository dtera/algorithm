package cn.cstn.algorithm.commons.util;

import cn.cstn.algorithm.commons.Tuple;
import cn.cstn.algorithm.commons.math.Point;

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

    public static List<Point> _merge(List<Point> ps) {
        List<Point> res = new ArrayList<>();
        if (ps == null || ps.size() < 1) return res;
        int n = ps.size();
        int[] ss = new int[n], es = new int[n];
        for (int i = 0; i < n; i++) {
            ss[i] = ps.get(i).getX();
            es[i] = ps.get(i).getY();
        }

        Arrays.sort(ss);
        Arrays.sort(es);

        for (int i = 0, j = 0; i < n; i++)
            if (i == n - 1 || ss[i + 1] > es[i]) {
                res.add(new Point(ss[j], es[i]));
                j = i + 1;
            }

        return res;
    }

    public static List<Point> merge(List<Point> ps) {
        List<Point> res = new ArrayList<>();
        if (ps == null || ps.size() < 1) return res;
        ps.sort(Comparator.comparingInt(Point::getX));
        //method1
        for (int i = 0, j = 0; i < ps.size(); i++)
            if (i == ps.size() - 1 || ps.get(i + 1).getX() > ps.get(i).getY()) {
                res.add(new Point(ps.get(j).getX(), ps.get(i).getY()));
                j = i + 1;
            }
        //method2
        /*Point p = ps.get(0);
        int s = p.getX(), e = p.getY();
        for (int i = 1; i < ps.size(); i++) {
            p = ps.get(i);
            if (p.getX() > e) {
                res.add(new Point(s, e));
                s = p.getX();
                e = p.getY();
            } else e = Math.max(p.getY(), e);
        }
        res.add(new Point(s, e));*/
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
