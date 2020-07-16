package cn.cstn.algorithm.offer;

/**
 * 给定一个数字，我们按照如下规则把它翻译为字符串：0 翻译成 “a” ，1 翻译成 “b”，……，11 翻译成 “l”，……，25 翻译成 “z”。
 * 一个数字可能有多个翻译。请编程实现一个函数，用来计算一个数字有多少种不同的翻译方法。
 * <p>
 * <p>
 * 示例 1:
 * <p>
 * 输入: 12258
 * <p>
 * 输出: 5
 * <p>
 * 解释: 12258有5种不同的翻译，分别是"bccfi", "bwfi", "bczi", "mcfi"和"mzi"
 * <p>  
 * <p>
 * 提示：
 * <p>
 * 0 <= num < 2^31
 * <p>
 *
 * @author zhaohuiqiang
 * @date 2020/7/15 11:21
 */
public class TranslateNum {

    public static void main(String[] args) {
        System.out.println(translateNum(1225816));
        System.out.println(translateNum_(1225816));
    }

    public static int translateNum(int num) {
        String s = num + "";
        int t, p = 0, res = 1;
        for (int i = 1; i < s.length(); i++) {
            t = res;
            if (s.charAt(i - 1) == '1' || (s.charAt(i - 1) == '2' && s.charAt(i) >= '0' && s.charAt(i) <= '5'))
                res += i == 1 ? 1 : p;
            p = t;
        }

        return res;
    }

    public static int translateNum_(int num) {
        String s = num + "";
        int[] dp = new int[s.length()];
        dp[0] = 1;
        for (int i = 1; i < dp.length; i++) {
            dp[i] = dp[i - 1];
            if (s.charAt(i - 1) == '1' || (s.charAt(i - 1) == '2' && s.charAt(i) >= '0' && s.charAt(i) <= '5'))
                dp[i] += i == 1 ? 1 : dp[i - 2];
        }

        return dp[dp.length - 1];
    }

}
