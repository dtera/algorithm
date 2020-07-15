package cn.cstn.algorithm.commons.util;

import org.apache.commons.lang3.tuple.MutablePair;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * description :        StringUtil class
 *
 * @author :            zhaohq
 * date :               2018/9/5 0005 20:29
 */
public class StringUtil {

    public static String lcs(String a, String b) {
        if (a == null || b == null) return null;

        int m = a.length(), n = b.length();
        int[][] dp = new int[m + 1][n + 1];
        int[][] status = new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                    status[i][j] = 1;
                } else {
                    if (dp[i][j - 1] >= dp[i - 1][j]) {
                        dp[i][j] = dp[i][j - 1];
                        status[i][j] = 2;
                    } else {
                        dp[i][j] = dp[i - 1][j];
                        status[i][j] = 3;
                    }
                }
            }
        }

        int k = dp[m][n], i = m, j = n;
        char[] res = new char[dp[m][n]];
        while (i > 0 && j > 0) {
            if (status[i][j] == 1) {
                res[--k] = a.charAt(i - 1);
                i--;
                j--;
            } else if (status[i][j] == 2) j--;
            else i--;
        }

        return new String(res);
    }

    public static void permutation(String a, Consumer<String> consumer) {
        ArrayUtil.permutation(ArrayUtil.primitiveToObj(a.toCharArray()), characters -> {
            StringBuilder sb = new StringBuilder();
            Arrays.stream(characters).forEach(sb::append);
            consumer.accept(sb.toString());
        });
    }

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

    public static String deleteCharByDictSort(String s, int k) {
        if (s == null) return null;

        StringBuilder res = new StringBuilder();
        TreeMap<Character, Integer> charMap = new TreeMap<>();
        for (int i = 0; i < s.length(); i++) {
            charMap.put(s.charAt(i), charMap.getOrDefault(s.charAt(i), 0) + 1);
        }
        char t = 0;
        for (Map.Entry<Character, Integer> entry : charMap.entrySet()) {
            if (entry.getValue() <= k) {
                k -= entry.getValue();
                charMap.put(entry.getKey(), 0);
                continue;
            }
            charMap.put(entry.getKey(), entry.getValue() - k);
            t = entry.getKey();
            break;
        }
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (charMap.get(c) != 0) {
                if (c == t) charMap.put(c, charMap.get(c) - 1);
                res.append(c);
            }
        }

        return res.toString();
    }

}
