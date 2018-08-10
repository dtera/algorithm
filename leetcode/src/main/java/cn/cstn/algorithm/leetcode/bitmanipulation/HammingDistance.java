package cn.cstn.algorithm.leetcode.bitmanipulation;

/**
 * 461               Hamming Distance
 * description :     The Hamming distance between two integers is the number of positions
 *                   at which the corresponding bits are different.
 *                   Given two integers x and y, calculate the Hamming distance.
 * note:
 *                   0 <= x, y < 231.
 * example:
 *                   Input: x = 1, y = 4
 *                   Output: 2
 *                   Explanation:
 *                   1   (0 0 0 1)
 *                   4   (0 1 0 0)
 *                          ↑   ↑
 *                   The above arrows point to positions where the corresponding bits are different.
 * @author :        zhaohq
 * date :            2018-07-26 18:50
 */
public class HammingDistance {
    public static void main(String[] args) {
        int x = 1, y = 6;
        System.out.println(hammingDistance(x, y));
        System.out.println(_hammingDistance(x, y));
        System.out.println(Integer.bitCount(x ^ y));
    }

    private static int _hammingDistance(int x, int y) {
        int z = x ^ y;

        if (z == 0) return 0;
        return z % 2 + _hammingDistance(x / 2, y / 2);
    }

    private static int hammingDistance(int x, int y) {
        int count = 0;
        int z = x ^ y;

        while (z != 0) {
            count += z % 2;
            z = z >>> 1;
        }

        return count;
    }
}
