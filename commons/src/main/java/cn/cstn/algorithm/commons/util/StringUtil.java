package cn.cstn.algorithm.commons.util;

import org.apache.commons.lang3.tuple.MutablePair;

import java.util.function.Predicate;

/**
 * description :        StringUtil class
 *
 * @author :            zhaohq
 * date :               2018/9/5 0005 20:29
 */
public class StringUtil {

    public static int longestPalindrome(String s) {
        if (s == null || s.length() == 0) return 0;
        int mx = 1, ml = 1, n = s.length(), id = 0;
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
                while (i - dp[i] >= 0 && i + dp[i] < cs.length && cs[i - dp[i]] == cs[i + dp[i]])
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
            t.setLeft((t.getLeft() + 1) % sp.length());
            return s.charAt(t.getRight()) != sp.charAt(t.getLeft());
        }) || isPairOfWords(n, t -> {
            t.setLeft((t.getLeft() - 1 + sp.length()) % sp.length());
            return s.charAt(t.getRight()) != sp.charAt(t.getLeft());
        });
    }

    private static boolean isPairOfWords(int n, Predicate<MutablePair<Integer, Integer>> p) {
        for (int i = 0; i < n; i++) {
            int j = 0;
            MutablePair<Integer, Integer> t = MutablePair.of(i, j);
            for (; j < n; j++) {
                t.setRight(j);
                if (p.test(t)) break;
            }
            if (j == n) return true;
        }
        return false;
    }

    public static String factorial(int n) {
        StringBuilder result = new StringBuilder("1");
        long c = 0;
        for (int i = 2; i <= n; i++) {
            for (int j = result.length() - 1; j >= 0; j--) {
                int k = result.charAt(j) - '0';
                long t = k * i + c;
                c = t / 10;
                result.setCharAt(j, (char) (t % 10 + '0'));
            }

            while (c != 0) {
                result.insert(0, c % 10);
                c /= 10;
            }
        }
        return result.toString();
    }

    public static String repeat(String sep, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(sep);
        }
        return sb.toString();
    }

}
