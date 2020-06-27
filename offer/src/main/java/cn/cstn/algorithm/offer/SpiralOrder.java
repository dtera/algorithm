package cn.cstn.algorithm.offer;

import cn.cstn.algorithm.commons.util.ArrayUtil;

/**
 * 输入一个矩阵，按照从外向里以顺时针的顺序依次打印出每一个数字。
 * <p>
 *  
 * <p>
 * 示例 1：
 * <p>
 * 输入：matrix = [[1,2,3],[4,5,6],[7,8,9]]
 * <p>
 * 输出：[1,2,3,6,9,8,7,4,5]
 * <p>
 * <p>
 * 示例 2：
 * <p>
 * 输入：matrix = [[1,2,3,4],[5,6,7,8],[9,10,11,12]]
 * <p>
 * 输出：[1,2,3,4,8,12,11,10,9,5,6,7]
 * <p>
 * <p>
 * 限制：
 * <p>
 * 0 <= matrix.length <= 100
 * <p>
 * 0 <= matrix[i].length <= 100
 * <p>
 *
 * @author zhaohuiqiang
 * @date 2020/6/27 20:30
 */
public class SpiralOrder {

    public static void main(String[] args) {
        int[][] matrix = {{1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}};
        int[] spiralOrder = spiralOrder(matrix);
        ArrayUtil.println(spiralOrder);
    }

    public static int[] spiralOrder(int[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) return new int[0];
        int m = matrix.length, n = matrix[0].length;
        int[] res = new int[m * n];
        int t = 0, d = m - 1, l = 0, r = n - 1, k = 0;

        while (t <= d && l <= r) {
            for (int i = l; i <= r; i++) {
                res[k++] = matrix[t][i];
            }
            for (int i = ++t; i <= d; i++) {
                res[k++] = matrix[i][r];
            }
            if (t <= d) {
                for (int i = --r; i >= l; i--) {
                    res[k++] = matrix[d][i];
                }
            }
            if (l <= r) {
                for (int i = --d; i >= t; i--) {
                    res[k++] = matrix[i][l];
                }
            }
            l++;
        }

        return res;
    }

}
