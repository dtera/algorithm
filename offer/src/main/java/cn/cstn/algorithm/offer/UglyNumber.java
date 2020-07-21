package cn.cstn.algorithm.offer;

/**
 * 我们把只包含质因子 2、3 和 5 的数称作丑数（Ugly Number）。求按从小到大的顺序的第 n 个丑数。
 * <p>
 * <p>
 * 示例:
 * <p>
 * 输入: n = 10
 * <p>
 * 输出: 12
 * <p>
 * 解释: 1, 2, 3, 4, 5, 6, 8, 9, 10, 12 是前 10 个丑数。
 * <p>
 * <p>
 * 说明:  
 * <p>
 * 1 是丑数。
 * <p>
 * n 不超过1690。
 * <p>
 *
 * @author zhaohuiqiang
 * @date 2020/7/20 17:09
 */
public class UglyNumber {

    public static void main(String[] args) {
        System.out.println(nthUglyNumber(10));
    }

    public static int nthUglyNumber(int n) {
        if (n < 1) return 0;

        int[] dp = new int[n];
        dp[0] = 1;
        int a = 0, b = 0, c = 0;
        for (int i = 1; i < n; i++) {
            int t2 = dp[a] * 2, t3 = dp[b] * 3, t5 = dp[c] * 5;
            dp[i] = Math.min(Math.min(t2, t3), t5);
            if (t2 == dp[i]) a++;
            if (t3 == dp[i]) b++;
            if (t5 == dp[i]) c++;
        }

        return dp[n - 1];
    }

}
