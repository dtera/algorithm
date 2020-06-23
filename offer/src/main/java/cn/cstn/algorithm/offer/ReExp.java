package cn.cstn.algorithm.offer;

/**
 * 请实现一个函数用来匹配包含'. '和'*'的正则表达式。模式中的字符'.'表示任意一个字符，而'*'表示它前面的字符可以出现任意次（含0次）。
 * 在本题中，匹配是指字符串的所有字符匹配整个模式。例如，字符串"aaa"与模式"a.a"和"ab*ac*a"匹配，但与"aa.a"和"ab*a"均不匹配。
 * <p>
 * <p>
 * 示例 1:
 * <p>
 * 输入:
 * <p>
 * s = "aa"
 * <p>
 * p = "a"
 * <p>
 * 输出: false
 * <p>
 * 解释: "a" 无法匹配 "aa" 整个字符串。
 * <p>
 * <p>
 * 示例 2:
 * <p>
 * 输入:
 * <p>
 * s = "aa"
 * <p>
 * p = "a*"
 * <p>
 * 输出: true
 * <p>
 * 解释: 因为 '*' 代表可以匹配零个或多个前面的那一个元素, 在这里前面的元素就是 'a'。因此，字符串 "aa" 可被视为 'a' 重复了一次。
 * <p>
 * <p>
 * 示例 3:
 * <p>
 * 输入:
 * <p>
 * s = "ab"
 * <p>
 * p = ".*"
 * <p>
 * 输出: true
 * <p>
 * 解释: ".*" 表示可匹配零个或多个（'*'）任意字符（'.'）。
 * <p>
 * <p>
 * 示例 4:
 * <p>
 * 输入:
 * <p>
 * s = "aab"
 * <p>
 * p = "c*a*b"
 * <p>
 * 输出: true
 * <p>
 * 解释: 因为 '*' 表示零个或多个，这里 'c' 为 0 个, 'a' 被重复一次。因此可以匹配字符串 "aab"。
 * <p>
 * <p>
 * 示例 5:
 * <p>
 * 输入:
 * <p>
 * s = "mississippi"
 * <p>
 * p = "mis*is*p*."
 * <p>
 * 输出: false
 * <p>
 * s 可能为空，且只包含从 a-z 的小写字母。
 * <p>
 * p 可能为空，且只包含从 a-z 的小写字母以及字符 . 和 *，无连续的 '*'。
 *
 * @author zhaohuiqiang
 * @date 2020/6/22 11:30
 */
public class ReExp {

    public static void main(String[] args) {
        String s = "mississippi";
        String p = "mis*is*p*.";
        System.out.println(isMatch(s, p));
    }

    public static boolean isMatch(String s, String p) {
        if (s == null || p == null) return false;
        int m = s.length(), n = p.length();
        boolean[][] dp = new boolean[m + 1][n + 1];

        for (int i = 0; i < m + 1; i++) {
            for (int j = 0; j < n + 1; j++) {
                if (j == 0) {
                    dp[i][j] = i == 0;
                } else {
                    if (p.charAt(j - 1) == '*') {
                        if (j > 1) {
                            dp[i][j] |= dp[i][j - 2];
                        }
                        if (i > 0 && j > 1 && (s.charAt(i - 1) == p.charAt(j - 2) || p.charAt(j - 2) == '.')) {
                            dp[i][j] |= dp[i - 1][j];
                        }
                    } else {
                        if (i > 0 && (s.charAt(i - 1) == p.charAt(j - 1) || p.charAt(j - 1) == '.')) {
                            dp[i][j] = dp[i - 1][j - 1];
                        }
                    }
                }
            }
        }

        return dp[m][n];
    }

}
