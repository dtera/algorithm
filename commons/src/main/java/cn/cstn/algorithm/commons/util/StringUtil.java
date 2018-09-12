package cn.cstn.algorithm.commons.util;

import cn.cstn.algorithm.commons.Tuple;

import java.util.function.Predicate;

/**
 * description :        StringUtil class
 * @author :            zhaohq
 * date :               2018/9/5 0005 20:29
 */
public class StringUtil {

    public static int longestPalindrome(String s) {
        if (s == null || s.length() == 0) return 0;
        int mx =1, ml = 1, n = s.length(), id = 0;
        char[] cs = new char[2 * n + 1];
        for (int i = 0; i < n; i++) {
            cs[2 * i] = '*';
            cs[2 * i + 1] = s.charAt(i);
        }
        cs[2 * n] = '*';

        int[] dp = new int[cs.length];
        dp[0] = dp[cs.length - 1] = 1;
        for (int i = 1; i < dp.length - 1; i++) {
            dp[i] = i < mx ? Math.min(dp[2 * id - i], mx - i) : 1;

            if (i + dp[i] >= mx) {
                while (i - dp[i] >=0 && i + dp[i] < cs.length && cs[i - dp[i]] == cs[i + dp[i]])
                    dp[i] += 1;
                mx = i + dp[i];
                id = i;
            }
            ml = Math.max(ml, dp[i]);
        }

        return ml - 1;
    }

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
