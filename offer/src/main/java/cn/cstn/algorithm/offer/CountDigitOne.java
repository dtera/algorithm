package cn.cstn.algorithm.offer;

/**
 * 输入一个整数 n ，求1～n这n个整数的十进制表示中1出现的次数。
 * <p>
 * <p>
 * 例如，输入12，1～12这些整数中包含1 的数字有1、10、11和12，1一共出现了5次。
 * <p>
 * <p>
 * 示例 1：
 * <p>
 * 输入：n = 12
 * <p>
 * 输出：5
 * <p>
 * <p>
 * 示例 2：
 * <p>
 * 输入：n = 13
 * <p>
 * 输出：6
 * <p>
 * <p>
 * 限制：
 * <p>
 * 1 <= n < 2^31
 * <p>
 *
 * @author zhaohuiqiang
 * @date 2020/7/10 11:03
 */
public class CountDigitOne {

    public static void main(String[] args) {
        System.out.println(countDigitOne(13));
    }

    public static int countDigitOne(int n) {
        int r = n / 10, c = n % 10, l = 0, d = 1, res = 0;

        while (r != 0 || c != 0) {
            if (c < 1) res += r * d;
            else if (c == 1) res += r * d + l + 1;
            else res += (r + 1) * d;
            l += c * d;
            c = r % 10;
            r /= 10;
            d *= 10;
        }

        return res;
    }

}
