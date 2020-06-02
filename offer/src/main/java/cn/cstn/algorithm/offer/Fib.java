package cn.cstn.algorithm.offer;

/**
 * 写一个函数，输入 n ，求斐波那契（Fibonacci）数列的第 n 项。斐波那契数列的定义如下：
 * <p>
 * F(0) = 0,   F(1) = 1
 * F(N) = F(N - 1) + F(N - 2), 其中 N > 1.
 * 斐波那契数列由 0 和 1 开始，之后的斐波那契数就是由之前的两数相加而得出。
 * <p>
 * 答案需要取模 1e9+7（1000000007），如计算初始结果为：1000000008，请返回 1。
 * <p>
 *  
 * <p>
 * 示例 1：
 * <p>
 * 输入：n = 2
 * <p>
 * 输出：1
 * <p>
 * 示例 2：
 * <p>
 * 输入：n = 5
 * <p>
 * 输出：5
 * <p>
 * <p>
 * 提示：
 * <p>
 * 0 <= n <= 100
 *
 * @author zhaohuiqiang
 * @date 2020/6/2 12:28
 */
public class Fib {

    public static void main(String[] args) {
        System.out.println(fib(45));
        System.out.println(_fib(45));
    }

    public static int fib(int n) {
        int a = 0, b = 1;
        for (int i = 0; i < n; i++) {
            int t = (a + b) % 1000000007;
            a = b;
            b = t;
        }
        return a;
    }

    public static int _fib(int n) {
        if (n < 2) return n;
        return (_fib(n - 2) + _fib(n - 1)) % 1000000007;
    }

}
