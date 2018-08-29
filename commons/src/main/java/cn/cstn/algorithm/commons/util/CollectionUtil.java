package cn.cstn.algorithm.commons.util;

import cn.cstn.algorithm.commons.math.Point;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

/**
 * description :        CollectionUtil
 * @author :           zhaohq
 * date :               2018/8/28 0028 20:39
 */
public class CollectionUtil {
    public static List<Point> merge(List<Point> ps) {
        List<Point> res = new ArrayList<>();
        ps.sort((p, q) -> {
            if (p.getX() > q.getX()) return 1;
            else if (p.getX() < q.getX()) return -1;
            return 0;
        });

        Point p = ps.get(0);
        int s = p.getX(), e = p.getY();
        for (int i = 1; i < ps.size(); i++) {
            p = ps.get(i);
            if (p.getX() > e) {
                res.add(new Point(s, e));
                s = p.getX();
                e = p.getY();
            }
            else e = Math.max(p.getY(), e);
        }
        res.add(new Point(s, e));
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
