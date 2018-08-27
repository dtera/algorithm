package cn.cstn.algorithm.corporation.bytedance;

import cn.cstn.algorithm.commons.graph.MUF;
import cn.cstn.algorithm.commons.util.ArrayUtil;

import java.util.Scanner;

/**
 * 头条8.12             世界杯开幕式
 * description :        世界杯开幕式会在球场C举行,球场C的球迷看台可以容纳M*N个球迷。在球场售票完成后,
 *                      现官方想统计此次开幕式一共有多少个球队球迷群体,最大的球队球迷群体有多少人。
 *                      经调研发现,球迷群体在选座时有以下特性:
 *                      ●  同球队的球迷群体会选择相邻座位,不同球队的球迷群体会选择不相邻的座位。
 *                          (注解:相邻包括前后相邻、左右相邻、斜对角相邻。)
 *                      ●  给定一个MN的二维球场,0代表该位置没有坐人,1代表该位置已有球迷,
 *                          希望输出球队群体个数P,最大的球队群体人数Q。
 *                      输入描述:
 *                      第一行,2个数字,M及N,使用英文逗号分割;
 *                      接下来M行,每行N的数字,使用英文逗号分割;
 *                      输出描述:
 *                      一行,2个数字,P及Q,使用英文逗号分割;
 *                      其中,P表示球队群体个数,Q表示最大的球队群体人数
 *                      输入:
 *                          10,10
 *                          0,0,0,0,0,0,0,0,0,0
 *                          0,0,0,1,1,0,1,0,0,0
 *                          0,1,0,0,0,0,0,1,0,1
 *                          1,0,0,0,0,0,0,0,1,1
 *                          0,0,0,1,1,1,0,0,0,1
 *                          0,0,0,0,0,0,1,0,1,1
 *                          0,1,1,0,0,0,0,0,0,0
 *                          0,0,0,1,0,1,0,0,0,0
 *                          0,0,1,0,0,1,0,0,0,0
 *                          0,1,0,0,0,0,0,0,0,0
 *                      输出:       6,8
 *                      备注:
 *                      数据范围    0<M<1000
 *                                  0<N<1000
 * @author :           zhaohq
 * date :               2018/8/27 0027 19:48
 */
public class WorldCup {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String line = sc.nextLine();
        String[] ls = line.split(",");
        int m = Integer.parseInt(ls[0]), n = Integer.parseInt(ls[0]);
        int[][] a = new int[m][n];
        for (int i = 0; i < m; i++) {
            line = sc.nextLine();
            ls = line.split(",");
            for (int j = 0; j < n; j++)
                a[i][j] = Integer.parseInt(ls[j]);
        }

        MUF muf = new MUF(a);
        muf.buildConnectedComponent();
        int[] nums = muf.getNums();
        System.out.println(muf.getNumOfComponent() + "," + nums[ArrayUtil.indexOfMinMax(nums)[1]]);
        System.out.println("======================================================================");

        int[][] b = new int[m][n];
        int[][] d = ArrayUtil.getWalkDirections();
        int maxNum = 0, numOfComponent = 0;
        for (int i = 0; i < m; i++)
            System.arraycopy(a[i], 0, b[i], 0, n);

        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                if (b[i][j] != 0) {
                    int num = 0;
                    num = dfs(b, i, j, num, d);
                    if (num > maxNum) maxNum = num;
                    numOfComponent++;
                }
        System.out.println(numOfComponent + "," + maxNum);
    }

    private static int dfs(int[][] b, int i, int j, int num, int[][] d) {
        num++;
        b[i][j] = 0;
        for (int[] dk : d) {
            int p = i + dk[0];
            int q = j + dk[1];
            if (p < 0 || p >= b.length || q < 0 || q >= b[0].length) continue;
            if (b[p][q] != 0)
                num = dfs(b, p, q, num, d);
        }

        return num;
    }

}
