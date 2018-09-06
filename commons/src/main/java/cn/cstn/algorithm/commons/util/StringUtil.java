package cn.cstn.algorithm.commons.util;

import cn.cstn.algorithm.commons.Tuple;

import java.util.function.Predicate;

/**
 * description :        StringUtil class
 * @author :            zhaohq
 * date :               2018/9/5 0005 20:29
 */
public class StringUtil {

    public static boolean isPairOfWords(String s, String sp) {
        assert s != null && sp != null && s.length() == sp.length();
        int n = s.length();

        return isPairOfWords(n, t -> {
            t.s_1((t._1() + 1) % sp.length());
            return s.charAt(t._2()) != sp.charAt(t._1());
        }) || isPairOfWords(n, t -> {
            t.s_1((t._1() - 1 + sp.length()) % sp.length());
            return s.charAt(t._2()) != sp.charAt(t._1());
        });
    }

    private static boolean isPairOfWords(int n, Predicate<Tuple<Integer>> p) {
        for (int i = 0; i < n; i++) {
            int j = 0;
            Tuple<Integer> t = new Tuple<>(i, j);
            for (; j < n; j++) {
                t.s_2(j);
                if (p.test(t)) break;
            }
            if (j == n) return true;
        }
        return false;
    }

}
