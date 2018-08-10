package cn.cstn.algorithm.corporation.netease;

import java.util.Scanner;

/**
 * description :
 *                      题目描述:
 *                              有 n 个学生站成一排，每个学生有一个能力值，牛牛想从这 n 个学生中按照顺序选取 k 名学生，
 *                              要求相邻两个学生的位置编号的差不超过 d，使得这 k 个学生的能力值的乘积最大，你能返回最大的乘积吗？
 *                      输入描述:
 *                              每个输入包含 1 个测试用例。每个测试数据的第一行包含一个整数 n (1 <= n <= 50)，表示学生的个数，
 *                              接下来的一行，包含 n 个整数，按顺序表示每个学生的能力值 ai（-50 <= ai <= 50）。
 *                              接下来的一行包含两个整数，k 和 d (1 <= k <= 10, 1 <= d <= 50)。
 *                      输出描述:
 *                              输出一行表示最大的乘积。
 *                      示例1
 *                              输入    3
 *                                      7 4 7
 *                                      2 50
 *                              输出    49
 * @author :           zhaohq
 * date :               2018/8/10 0010 9:53
 */
public class Chorus {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int[] arr = new int[n];
        for (int i = 0; i < n; i++)
            arr[i] = sc.nextInt();
        int k = sc.nextInt();
        int d = sc.nextInt();

        long res = selectMaxPowerMulti(arr, k, d);
        System.out.println(res);
    }

    private static long selectMaxPowerMulti(int[] arr, int k, int d) {
        long res = Long.MIN_VALUE;
        int n = arr.length;
        long[][] f = new long[k][n];
        long[][] g = new long[k][n];
        for (int i = 0; i < n; i++)
            f[0][i] = g[0][i] = arr[i];

        for (int i = 1; i < k; i++) {
            for (int j = i; j < n; j++) {
                long curmax = Long.MIN_VALUE;
                long curmin = Long.MAX_VALUE;
                for (int l = Math.max(i - 1, j - d); l < j; l++) {
                    long cf = f[i - 1][l] * arr[j];
                    long cg = g[i - 1][l] * arr[j];
                    if (Math.max(cf, cg) > curmax) curmax = Math.max(cf, cg);
                    if (Math.min(cf, cg) < curmin) curmin = Math.min(cf, cg);
                }
                f[i][j] = curmax;
                g[i][j] = curmin;
            }
        }

        for (int i = k - 1; i < n; i++)
            if (f[k - 1][i] > res) res = f[k - 1][i];

        return res;
    }
}
