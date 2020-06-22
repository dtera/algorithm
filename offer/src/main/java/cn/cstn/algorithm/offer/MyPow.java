package cn.cstn.algorithm.offer;

/**
 * 实现函数double Power(double base, int exponent)，求base的exponent次方。不得使用库函数，同时不需要考虑大数问题。
 * <p>
 * <p>
 * 示例 1:
 * <p>
 * 输入: 2.00000, 10
 * <p>
 * 输出: 1024.00000
 * <p>
 * <p>
 * 示例 2:
 * <p>
 * 输入: 2.10000, 3
 * <p>
 * 输出: 9.26100
 * <p>
 * <p>
 * 示例 3:
 * <p>
 * 输入: 2.00000, -2
 * <p>
 * 输出: 0.25000
 * <p>
 * 解释: 2-2 = 1/22 = 1/4 = 0.25
 * <p>
 * <p>
 * 说明:
 * <p>
 * -100.0 < x < 100.0
 * <p>
 * n 是 32 位有符号整数，其数值范围是 [−2^31, 2^31 − 1] 。
 *
 * @author zhaohuiqiang
 * @date 2020/6/19 18:18
 */
public class MyPow {

    public static void main(String[] args) {
        System.out.println(myPow(2.1, 3));
    }

    public static double myPow(double x, int n) {
        if (x == 0) return 0;

        double res = 1;
        long t = n;
        if (t < 0) {
            x = 1 / x;
            t = -t;
        }
        while (t > 0) {
            if ((t & 1) == 1) res *= x;
            x *= x;
            t >>= 1;
        }

        return res;
    }

}
