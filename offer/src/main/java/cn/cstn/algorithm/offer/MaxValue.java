package cn.cstn.algorithm.offer;

/**
 * 在一个 m*n 的棋盘的每一格都放有一个礼物，每个礼物都有一定的价值（价值大于 0）。你可以从棋盘的左上角开始拿格子里的礼物，
 * 并每次向右或者向下移动一格、直到到达棋盘的右下角。给定一个棋盘及其上面的礼物的价值，请计算你最多能拿到多少价值的礼物？
 * <p>
 * <p>
 * 示例 1:
 * <p>
 * 输入:
 * <p>
 * [
 * <p>
 *   [1,3,1],
 * <p>
 *   [1,5,1],
 * <p>
 *   [4,2,1]
 * <p>
 * ]
 * <p>
 * 输出: 12
 * <p>
 * 解释: 路径 1→3→5→2→1 可以拿到最多价值的礼物
 * <p>  
 * <p>
 * 提示：
 * <p>
 * 0 < grid.length <= 200
 * 0 < grid[0].length <= 200
 * <p>
 *
 * @author zhaohuiqiang
 * @date 2020/7/17 9:26
 */
public class MaxValue {

    public static void main(String[] args) {
        int[][] grid = {
                {1, 3, 1},
                {1, 5, 1},
                {4, 2, 1}
        };
        System.out.println(maxValue(grid));
    }

    public static int maxValue(int[][] grid) {
        int m = grid.length, n = grid[0].length;

        for (int i = 1; i < m; i++) {
            grid[i][0] += grid[i - 1][0];
        }
        for (int i = 1; i < n; i++) {
            grid[0][i] += grid[0][i - 1];
        }
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                grid[i][j] += Math.max(grid[i - 1][j], grid[i][j - 1]);
            }
        }

        return grid[m - 1][n - 1];
    }

}
