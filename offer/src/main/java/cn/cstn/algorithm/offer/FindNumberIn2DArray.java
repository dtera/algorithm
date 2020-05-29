package cn.cstn.algorithm.offer;

/**
 * 在一个 n * m 的二维数组中，每一行都按照从左到右递增的顺序排序，每一列都按照从上到下递增的顺序排序。
 * 请完成一个函数，输入这样的一个二维数组和一个整数，判断数组中是否含有该整数。
 * <p>
 *  
 * <p>
 * 示例:
 * <p>
 * 现有矩阵 matrix 如下：
 * <p>
 * [
 * <p>
 * &nbsp;&nbsp;&nbsp;&nbsp;[1,   4,  7, 11, 15],
 * <p>
 * &nbsp;&nbsp;&nbsp;&nbsp;[2,   5,  8, 12, 19],
 * <p>
 * &nbsp;&nbsp;&nbsp;&nbsp;[3,   6,  9, 16, 22],
 * <p>
 * &nbsp;&nbsp;&nbsp;&nbsp;[10, 13, 14, 17, 24],
 * <p>
 * &nbsp;&nbsp;&nbsp;&nbsp;[18, 21, 23, 26, 30]
 * <p>
 * ]
 * <p>
 * 给定 target = 5，返回 true。
 * <p>
 * 给定 target = 20，返回 false。
 * <p>
 *  
 * <p>
 * 限制：
 * <p>
 * 0 <= n <= 1000
 * <p>
 * 0 <= m <= 1000
 *
 * @author zhaohuiqiang
 * @date 2020/5/29 11:10
 */
public class FindNumberIn2DArray {
    public static void main(String[] args) {
        int[][] matrix = new int[][]{{1, 4, 7, 11, 15},
                {2, 5, 8, 12, 19},
                {3, 6, 9, 16, 22},
                {10, 13, 14, 17, 24},
                {18, 21, 23, 26, 30}};
        System.out.println(_findNumberIn2DArray(matrix, 20));
        System.out.println(findNumberIn2DArray(matrix, 14));
    }

    public static boolean findNumberIn2DArray(int[][] matrix, int target) {
        return findNumberIn2DArray(matrix, target, 0, matrix.length - 1, 0, matrix[0].length - 1);
    }

    public static boolean findNumberIn2DArray(int[][] matrix, int target, int t, int b, int l, int r) {
        if (t < 0 || t >= matrix.length || b < 0 || b >= matrix.length ||
                l < 0 || l >= matrix[0].length || r < 0 || r >= matrix[0].length ||
                t > b || l > r) {
            return false;
        }
        int i = (t + b) / 2, j = (l + r) / 2;
        if (matrix[i][j] < target) {
            return findNumberIn2DArray(matrix, target, t, i, j + 1, r) ||
                    findNumberIn2DArray(matrix, target, i + 1, b, l, j) ||
                    findNumberIn2DArray(matrix, target, i + 1, b, j + 1, r);
        }
        if (matrix[i][j] > target) {
            return findNumberIn2DArray(matrix, target, i, b, l, j - 1) ||
                    findNumberIn2DArray(matrix, target, t, i - 1, l, j - 1) ||
                    findNumberIn2DArray(matrix, target, t, i - 1, j, r);
        }
        return true;
    }

    public static boolean _findNumberIn2DArray(int[][] matrix, int target) {
        if (matrix == null || matrix.length == 0) {
            return false;
        }
        int i = 0, j = matrix[0].length - 1;
        while (i < matrix.length && j >= 0) {
            if (matrix[i][j] == target) {
                return true;
            } else if (matrix[i][j] < target) {
                i++;
            } else {
                j--;
            }
        }
        return false;
    }

}
