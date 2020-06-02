package cn.cstn.algorithm.offer;

/**
 * 一只青蛙一次可以跳上1级台阶，也可以跳上2级台阶。求该青蛙跳上一个 n 级的台阶总共有多少种跳法。
 * <p>
 * 答案需要取模 1e9+7（1000000007），如计算初始结果为：1000000008，请返回 1。
 *
 * <p>
 * 示例 1：
 * <p>
 * 输入：n = 2
 * <p>
 * 输出：2
 * <p>
 * 示例 2：
 * <p>
 * 输入：n = 7
 * <p>
 * 输出：21
 * <p>
 * <p>
 * 提示：
 * <p>
 * 0 <= n <= 10
 *
 * @author zhaohuiqiang
 * @date 2020/6/2 14:58
 */
public class NumWays {

    public static void main(String[] args) {
        System.out.println(numWays(7));
        System.out.println(_numWays(7));
    }

    public static int numWays(int n) {
        int a = 1, b = 1;
        for (int i = 0; i < n; i++) {
            int t = (a + b) % 1000000007;
            a = b;
            b = t;
        }
        return a;
    }

    public static int _numWays(int n) {
        if (n < 2) return 1;
        return (_numWays(n - 2) + _numWays(n - 1)) % 1000000007;
    }

}
