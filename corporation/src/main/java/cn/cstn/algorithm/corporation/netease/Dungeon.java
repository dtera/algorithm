package cn.cstn.algorithm.corporation.netease;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

/**
 * description :
 *                      题目描述
 *                              给定一个 n 行 m 列的地牢，其中 '.' 表示可以通行的位置，'X' 表示不可通行的障碍，
 *                              牛牛从 (x0 , y0 ) 位置出发，遍历这个地牢，和一般的游戏所不同的是，
 *                              他每一步只能按照一些指定的步长遍历地牢，要求每一步都不可以超过地牢的边界，也不能到达障碍上。
 *                              地牢的出口可能在任意某个可以通行的位置上。牛牛想知道最坏情况下，他需要多少步才可以离开这个地牢。
 *                      输入描述:
 *                              每个输入包含 1 个测试用例。每个测试用例的第一行包含两个整数 n 和 m（1 <= n, m <= 50），
 *                              表示地牢的长和宽。接下来的 n 行，每行 m 个字符，描述地牢，地牢将至少包含两个 '.'。
 *                              接下来的一行，包含两个整数 x0, y0，表示牛牛的出发位置（0 <= x0 < n, 0 <= y0 < m，
 *                              左上角的坐标为 （0, 0），出发位置一定是 '.'）。之后的一行包含一个整数 k（0 < k <= 50）
 *                              表示牛牛合法的步长数，接下来的 k 行，每行两个整数 dx, dy 表示每次可选择移动的行和列步长
 *                              （-50 <= dx, dy <= 50）
 *                      输出描述:
 *                              输出一行一个数字表示最坏情况下需要多少次移动可以离开地牢，如果永远无法离开，输出 -1。
 *                              以下测试用例中，牛牛可以上下左右移动，在所有可通行的位置.上，地牢出口如果被设置在右下角，
 *                              牛牛想离开需要移动的次数最多，为3次。
 *                      示例1
 *                              输入
 *                                      3 3
 *                                      ...
 *                                      ...
 *                                      ...
 *                                      0 1
 *                                      4
 *                                      1 0
 *                                      0 1
 *                                      -1 0
 *                                      0 -1
 *                              输出
 *                                      3
 * @author :           zhaohq
 * date :               2018/8/10 0010 17:43
 */
public class Dungeon {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (sc.hasNext()) {
            int n = sc.nextInt();
            int m = sc.nextInt();

            char[][] dungeon = new char[n][m];
            for (int i = 0; i < n; i++) {
                String line = sc.next();
                dungeon[i] = line.toCharArray();
            }

            int x0 = sc.nextInt();
            int y0 = sc.nextInt();
            int k = sc.nextInt();
            int[] dxs = new int[k];
            int[] dys = new int[k];
            for (int i = 0; i < k; i++) {
                dxs[i] = sc.nextInt();
                dys[i] = sc.nextInt();
            }

            int max = dungeonEscape(dungeon, x0, y0, dxs, dys);
            System.out.println(max);
        }
    }

    private static int dungeonEscape(char[][] dungeon, int x0, int y0, int[] dxs, int[] dys) {
        int n = dungeon.length, m = dungeon[0].length, k = dxs.length, max = 0;
        int[][] accFlags = new int[n][m];
        Queue<Integer> qx = new LinkedList<>();
        Queue<Integer> qy = new LinkedList<>();

        qx.add(x0);
        qy.add(y0);

        accFlags[x0][y0] = 1;
        while (!qx.isEmpty() && !qy.isEmpty()) {
            x0 = qx.remove();
            y0 = qy.remove();
            for (int i = 0; i < k; i++) {
                if (x0 + dxs[i] < n && x0 + dxs[i] >= 0 && y0 + dys[i] < m && y0 + dys[i] >= 0
                        && accFlags[x0 + dxs[i]][y0 + dys[i]] == 0)
                    if (dungeon[x0 + dxs[i]][y0 + dys[i]] == '.') {
                        accFlags[x0 + dxs[i]][y0 + dys[i]] = accFlags[x0][y0] + 1;
                        qx.add(x0 + dxs[i]);
                        qy.add(y0 + dys[i]);
                    } else
                        accFlags[x0 + dxs[i]][y0 + dys[i]] = -1;
            }
        }

        boolean hasRoad = true;
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++) {
                if (dungeon[i][j] == '.' && accFlags[i][j] == 0) {
                    hasRoad = false;
                }
                max = Math.max(max, accFlags[i][j]);
            }

        if (hasRoad)
            return max - 1;
        else
            return -1;
    }
}
