package cn.cstn.algorithm.offer;

/**
 * 请实现一个函数，输入一个整数，输出该数二进制表示中 1 的个数。例如，把 9 表示成二进制是 1001，有 2 位是 1。
 * 因此，如果输入 9，则该函数输出 2。
 * <p>
 * <p>
 * 示例 1：
 * <p>
 * 输入：00000000000000000000000000001011
 * <p>
 * 输出：3
 * <p>
 * 解释：输入的二进制串 00000000000000000000000000001011 中，共有三位为 '1'。
 * <p>
 * <p>
 * 示例 2：
 * <p>
 * 输入：00000000000000000000000010000000
 * <p>
 * 输出：1
 * <p>
 * 解释：输入的二进制串 00000000000000000000000010000000 中，共有一位为 '1'。
 * <p>
 * <p>
 * 示例 3：
 * <p>
 * 输入：11111111111111111111111111111101
 * <p>
 * 输出：31
 * <p>
 * 解释：输入的二进制串 11111111111111111111111111111101 中，共有 31 位为 '1'。
 *
 * @author zhaohuiqiang
 * @date 2020/6/19 17:54
 */
public class HammingWeight {

    public static void main(String[] args) {
        System.out.println(hammingWeight(31));
        System.out.println(hammingWeight_(31));
    }

    public static int hammingWeight(int n) {
        int res = 0;
        while (n != 0) {
            res++;
            n &= (n - 1);
        }
        return res;
    }

    public static int hammingWeight_(int n) {
        int res = 0;
        while (n != 0) {
            res += n & 1;
            n >>= 1;
        }
        return res;
    }

}
