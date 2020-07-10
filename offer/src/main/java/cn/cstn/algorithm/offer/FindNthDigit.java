package cn.cstn.algorithm.offer;

/**
 * 数字以0123456789101112131415…的格式序列化到一个字符序列中。在这个序列中，第5位（从下标0开始计数）是5，第13位是1，第19位是4，等等。
 * <p>
 * 请写一个函数，求任意第n位对应的数字。
 * <p>
 * <p>
 * 示例 1：
 * <p>
 * 输入：n = 3
 * <p>
 * 输出：3
 * <p>
 * <p>
 * 示例 2：
 * <p>
 * 输入：n = 11
 * <p>
 * 输出：0
 * <p>  
 * <p>
 * 限制：
 * <p>
 * 0 <= n < 2^31
 * <p>
 *
 * @author zhaohuiqiang
 * @date 2020/7/10 14:07
 */
public class FindNthDigit {

    public static void main(String[] args) {
        System.out.println(findNthDigit(1000000000));
    }

    public static int findNthDigit(int n) {
        if (n < 10) return n;
        int b = 1, d = 1;
        long r = 9 * b * d + 1;
        while (n >= r) {
            n -= r;
            b++;
            d *= 10;
            r = 9L * b * d;
        }
        int num = d + n / b;
        return String.valueOf(num).charAt(n % b) - '0';
    }

}
