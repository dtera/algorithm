package cn.cstn.algorithm.offer;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 地上有一个m行n列的方格，从坐标 [0,0] 到坐标 [m-1,n-1] 。一个机器人从坐标 [0, 0] 的格子开始移动，
 * 它每次可以向左、右、上、下移动一格（不能移动到方格外），也不能进入行坐标和列坐标的数位之和大于k的格子。
 * 例如，当k为18时，机器人能够进入方格 [35, 37] ，因为3+5+3+7=18。但它不能进入方格 [35, 38]，
 * 因为3+5+3+8=19。请问该机器人能够到达多少个格子？
 * <p> 
 * <p>
 * 示例 1：
 * <p>
 * 输入：m = 2, n = 3, k = 1
 * <p>
 * 输出：3
 * <p>
 * <p>
 * 示例 2：
 * <p>
 * 输入：m = 3, n = 1, k = 0
 * <p>
 * 输出：1
 * <p>
 * 提示：
 * <p>
 * 1 <= n,m <= 100
 * <p>
 * 0 <= k <= 20
 *
 * @author zhaohuiqiang
 * @date 2020/6/15 11:36
 */
public class MovingCount {

    public static void main(String[] args) {
        int m = 2, n = 3, k = 1;
        System.out.println(movingCount(m, n, k));
    }

    @SuppressWarnings("all")
    public static int movingCount(int m, int n, int k) {
        int count = 1;
        Queue<int[]> queue = new LinkedList<int[]>() {{
            add(new int[]{0, 0});
        }};
        int[][] ds = new int[][]{{0, 1}, {1, 0}};
        boolean[][] visit = new boolean[m][n];
        visit[0][0] = true;
        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            assert cur != null;
            for (int[] d : ds) {
                int x = cur[0] + d[0];
                int y = cur[1] + d[1];
                if (x >= m || y >= n || visit[x][y] || sum(x) + sum(y) > k) continue;
                count++;
                visit[x][y] = true;
                queue.add(new int[]{x, y});
            }
        }

        return count;
    }

    private static int sum(int x) {
        int res = x % 10;
        x /= 10;
        while (x != 0) {
            res += x % 10;
            x /= 10;
        }
        return res;
    }

}
