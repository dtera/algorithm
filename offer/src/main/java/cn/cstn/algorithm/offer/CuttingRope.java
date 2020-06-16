package cn.cstn.algorithm.offer;

/**
 * CuttingRope
 *
 * @author zhaohuiqiang
 * @date 2020/6/15 16:29
 */
@SuppressWarnings("unused")
public class CuttingRope {

    public static void main(String[] args) {
        System.out.println(cuttingRope_(120));
        System.out.println(cuttingRope(120));
    }

    /**
     * 给你一根长度为 n 的绳子，请把绳子剪成整数长度的 m 段（m、n都是整数，n>1并且m>1），每段绳子的长度记为 k[0],k[1]...k[m] 。
     * 请问 k[0]*k[1]*...*k[m] 可能的最大乘积是多少？例如，当绳子的长度是8时，我们把它剪成长度分别为2、3、3的三段，
     * 此时得到的最大乘积是18。
     * <p>
     * <p>
     * 答案需要取模 1e9+7（1000000007），如计算初始结果为：1000000008，请返回 1。
     * <p>
     *  
     * <p>
     * 示例 1：
     * <p>
     * 输入: 2
     * <p>
     * 输出: 1
     * <p>
     * 解释: 2 = 1 + 1, 1 × 1 = 1
     * <p>
     * <p>
     * 示例 2:
     * <p>
     * 输入: 10
     * <p>
     * 输出: 36
     * <p>
     * 解释: 10 = 3 + 3 + 4, 3 × 3 × 4 = 36
     *  
     * <p>
     * <p>
     * 提示：
     * <p>
     * 2 <= n <= 1000
     */
    public static int cuttingRope_(int n) {
        if (n < 4) return n - 1;
        int i = n / 3, j = n % 3, p = 1000000007;
        if (j == 1) return (int) ((remainder_(3, i - 1, p) * 4) % p);
        return (int) ((remainder_(3, i, p) * (j == 0 ? 1 : 2)) % p);
    }

    public static long remainder_(long x, int n, int p) {
        long r = 1;
        while (n > 0) {
            if (n % 2 == 1) r = (r * x) % p;
            x = (x * x) % p;
            n /= 2;
        }
        return r;
    }

    public static long remainder(long x, int n, int p) {
        long r = 1;
        for (int i = 0; i < n; i++) {
            r = (r * x) % p;
        }
        return r;
    }

    /**
     * 给你一根长度为 n 的绳子，请把绳子剪成整数长度的 m 段（m、n都是整数，n>1并且m>1），每段绳子的长度记为 k[0],k[1]...k[m-1] 。
     * 请问 k[0]*k[1]*...*k[m-1] 可能的最大乘积是多少？例如，当绳子的长度是8时，我们把它剪成长度分别为2、3、3的三段，
     * 此时得到的最大乘积是18。
     * <p>
     * 示例 1：
     * <p>
     * 输入: 2
     * <p>
     * 输出: 1
     * <p>
     * 解释: 2 = 1 + 1, 1 × 1 = 1
     * <p>
     * <p>
     * 示例 2:
     * <p>
     * 输入: 10
     * <p>
     * 输出: 36
     * <p>
     * 解释: 10 = 3 + 3 + 4, 3 × 3 × 4 = 36
     * <p>
     * <p>
     * 提示：
     * <p>
     * 2 <= n <= 58
     */
    public static int cuttingRope(int n) {
        if (n < 4) return n - 1;
        int i = n / 3, j = n % 3;
        if (j == 1) return (int) (Math.pow(3, i - 1) * 4);
        return (int) (Math.pow(3, i) * (j == 0 ? 1 : 2));
    }

}
