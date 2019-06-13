package cn.cstn.algorithm.corporation.bytedance;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

/**
 * 头条8.12             5.直播爱好者
 * description :        小明在抖音关注了N个主播，每个主播每天的开播时间是固定的，分别在si时刻开始开播，
 *                      ti时刻结束。小明无法同时看两个主播的直播。一天被分为M个时间单位。
 *                      请问小明每天最多能完整观看多少场直播？
 *                      输入描述：
 *                          第一行一个整数，代表N
 *                          第二行一个整数，代表M
 *                          第三行空格分隔N*2个整数，代表s,t
 *                      输出描述： 一行一个整数，表示答案
 *                      示例1
 *                      输入：
 *                          3
 *                          10
 *                          0 3 3 7 7 0
 *                      输出：3
 *                      示例2
 *                      输入：
 *                          3
 *                          10
 *                          0 5 2 7 6 9
 *                      输出：2
 *                      数据范围：
 *                          1 <= N <= 10^5
 *                          2 <= M <= 10^6
 *                          0 <= si,ti < M （si != ti）
 *                          si > ti 代表时间跨天，但直播时长不会超过一天
 * @author :            zhaohq
 * date :               2018/8/29 0029 14:27
 */
@SuppressWarnings("unchecked")
public class LiveBroadcastFan {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int m = sc.nextInt();
        Pair<Integer, Integer>[] ts = new Pair[n];
        for (int i = 0; i < n; i++) {
            ts[i] = Pair.of(sc.nextInt(), sc.nextInt());
            assert ts[i].getLeft() < m && ts[i].getRight() < m;
        }

        System.out.println(numOfWatchLiveBroadcast(ts, m));
    }

    private static int numOfWatchLiveBroadcast(Pair<Integer, Integer>[] ts, int m) {
        if (ts == null) return 0;
        int num = 1, e = ts[0].getRight();
        if (ts[0].getLeft() > ts[0].getRight()) e += m;
        Arrays.sort(ts, Comparator.comparingInt(Pair::getLeft));
        for (int i = 1; i < ts.length; i++)
            if (ts[i].getLeft() >= e && (ts[i].getLeft() <= ts[i].getRight() || i == ts.length - 1)) num++;
            else if (ts[i].getLeft() <= ts[i].getRight())
                e = Math.min(e, ts[i].getRight());

        return num;
    }
}
