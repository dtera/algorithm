package cn.cstn.algorithm.corporation.kuaishou;

import cn.cstn.algorithm.commons.util.ArrayUtil;

import java.util.*;

/**
 * Maze
 *
 * @author zhaohuiqiang
 * @date 2020/6/25 14:40
 */
public class Maze {

    public static void main(String[] args) {
        int[][] maze = {
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 0, 1, 1},
                {1, 1, 1, 0, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
        };
        int[][] res = maze(maze);
        Arrays.stream(maze).forEach(ArrayUtil::println);
        System.out.println();
        Arrays.stream(res).forEach(ArrayUtil::println);
    }

    public static int[][] maze(int[][] maze) {
        if (maze == null) return null;
        int m = maze.length, n = maze[0].length;
        int[][] res = new int[m][n];
        boolean[][] visits = new boolean[m][n];
        Queue<int[]> queue = new LinkedList<>();

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (maze[i][j] == 0) {
                    queue.add(new int[]{i, j});
                    visits[i][j] = true;
                } else {
                    res[i][j] = Integer.MAX_VALUE;
                }
            }
        }
        while (!queue.isEmpty()) {
            int[] p = queue.poll();
            int[] up = new int[]{p[0] - 1, p[1]};
            int[] down = new int[]{p[0] + 1, p[1]};
            int[] left = new int[]{p[0], p[1] - 1};
            int[] right = new int[]{p[0], p[1] + 1};

            bfs(p, up, visits, queue, res);
            bfs(p, down, visits, queue, res);
            bfs(p, left, visits, queue, res);
            bfs(p, right, visits, queue, res);
        }

        return res;
    }

    private static void bfs(int[] p, int[] cur, boolean[][] visits, Queue<int[]> queue, int[][] res) {
        int m = res.length, n = res[0].length;
        if (cur[0] >= 0 && cur[0] < m && cur[1] >= 0 && cur[1] < n &&
                !visits[cur[0]][cur[1]] && res[p[0]][p[1]] + 1 < res[cur[0]][cur[1]]) {
            visits[cur[0]][cur[1]] = true;
            queue.add(new int[]{cur[0], cur[1]});
            res[cur[0]][cur[1]] = res[p[0]][p[1]] + 1;
        }
    }

}
