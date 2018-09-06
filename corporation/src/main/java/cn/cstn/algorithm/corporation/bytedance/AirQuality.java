package cn.cstn.algorithm.corporation.bytedance;

import java.util.Scanner;

/**
 * 8.25                 4.空气质量
 * description :        一天，小凯同学震惊的发现，自己屋内的PM2.5指标是有规律的！小凯采样了PM2.5数值，发现
 *                      PM2.5数值以小时为周期循环，即任意时刻的PM2.5总是和一小时前相等！他的室友小文同学提
 *                      出了这样一个问题，在t小时内的所有采样点中，选取若干采样点的数值，能否找到一个PM2.5
 *                      不曾下降过的序列？这个序列最长是多少？
 *                      输入描述：
 *                          第一行有两个整数n和t，表示每小时的采样点的个数，和询问多少个小时的结果。
 *                          第二行有n个整数，已空格分隔，表示一个小时内，每个采样点观测到的PM2.5数值。
 *                      输出描述：
 *                          一个整数，表示T小时内，最长的PM2.5不曾下降过的序列的长度。
 *                      示例1：
 *                          输入
 *                          4 3
 *                          10 3 7 5
 *                          输出 4
 *                          说明
 *                          3小时内的所有采样点为10 3 7 5 10 3 7 5 10 3 7 5 选取第2,3,5,9个采样点，可以得到
 *                          一个不曾下降过的序列3 7 10 10，使用其他方法也可以得到长为4的满足条件的序列，但
 *                          无法得到长度超过4 的结果。
 *                      备注：
 *                          对于20%的数据，t=1，对于50%的数据，t<=1000,对于80%的数据，PM2.5数值不超过200，
 *                          对于100%的数据，1<=n<=1000,1<=t<=1000000,PM2.5数值为正整数，且不超过1000000000
 * @author :            zhaohq
 * date :               2018/9/6 0006 15:12
 */
public class AirQuality {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int t = sc.nextInt();
        int[] a = new int[n];
        for (int i = 0; i < n; i++)
            a[i] = sc.nextInt();

        System.out.println(lis(a, t));
    }

    private static int lis(int[] a, int t) {
        return 0;
    }

}
