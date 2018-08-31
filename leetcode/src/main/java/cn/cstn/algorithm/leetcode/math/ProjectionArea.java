package cn.cstn.algorithm.leetcode.math;

/**
 * 887                  Projection Area of 3D Shapes
 * description :        On a N * N grid, we place some 1 * 1 * 1 cubes that are axis-aligned with the x, y, and z axes.
 *                      Each value v = grid[i][j] represents a tower of v cubes placed on top of grid cell (i, j).
 *                      Now we view the projection of these cubes onto the xy, yz, and zx planes.
 *                      A projection is like a shadow, that maps our 3 dimensional figure to a 2 dimensional plane.
 *                      Here, we are viewing the "shadow" when looking at the cubes from the top, the front, and the side.
 *                      Return the total area of all three projections.
 * Example 1:
 *                      Input: [[2]]
 *                      Output: 5
 * Example 2:
 *                      Input: [[1,2],[3,4]]
 *                      Output: 17
 * Explanation:
 *                      Here are the three projections ("shadows") of the shape made with each axis-aligned plane.
 * Example 3:
 *                      Input: [[1,0],[0,2]]
 *                      Output: 8
 * Example 4:
 *                      Input: [[1,1,1],[1,0,1],[1,1,1]]
 *                      Output: 14
 * Example 5:
 *                      Input: [[2,2,2],[2,1,2],[2,2,2]]
 *                      Output: 21
 * Note:
 *                      1 <= grid.length = grid[0].length <= 50
 *                      0 <= grid[i][j] <= 50
 * @author :            zhaohq
 * date :               2018/8/12 0012 12:50
 */
public class ProjectionArea {
    public static void main(String[] args) {
        int[][] grid = {{2,2,2},{2,1,2},{2,2,2}};
        System.out.println(_projectionArea(grid));
        System.out.println(projectionArea(grid));
    }

    private static int _projectionArea(int[][] grid) {
        if (grid == null || grid.length == 0) return 0;
        int res = 0, n = grid.length;
        for (int i = 0; i < n; i++) {
            int rMax = 0;
            int cMax = 0;
            for (int j = 0; j < n; j++) {
                if (grid[i][j] > 0) res++;
                if (grid[i][j] > rMax) rMax = grid[i][j];
                if (grid[j][i] > cMax) cMax = grid[j][i];
            }
            res += rMax + cMax;
        }

        return res;
    }

    private static int projectionArea(int[][] grid) {
        if (grid == null || grid.length == 0) return 0;
        int res = 0;
        int[] yMax = new int[grid[0].length];
        for (int[] a : grid) {
            int xMaxi = 0;
            for (int j = 0; j < grid[0].length; j++) {
                if (a[j] > 0) res++;
                if (a[j] > xMaxi) xMaxi = a[j];
                if (a[j] > yMax[j]) yMax[j] = a[j];
            }
            res += xMaxi;
        }
        for (int y : yMax) {
            res += y;
        }

        return res;
    }
}
