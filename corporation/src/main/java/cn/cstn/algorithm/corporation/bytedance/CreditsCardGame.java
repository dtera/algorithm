package cn.cstn.algorithm.corporation.bytedance;

import cn.cstn.algorithm.commons.util.ArrayUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * 头条8.12             3.积分卡牌游戏
 * description :        小a和小b玩一个游戏，有n张卡牌，每张上面有两个正整数x,y。取一张牌时，
 * 个人积分增加x，团队积分增加y。求小a，小b各取若干张牌，使得他们的个人积分相等，
 * 且团队(小a、小b属于同一团队)积分最大。
 * 输入描述：
 * 第一行n, 接下来n行，每行两个整数x,y
 * 输出描述：
 * 一行一个整数,表示小a的积分和小b的积分相等的时候，团队积分的最大值。
 * 输入:
 * 4
 * 3 1
 * 2 2
 * 1 4
 * 1 4
 * 输出: 10
 * 数据范围:
 * 0 < n < 100
 * 0 < x < 1000
 * 0 < y < 1e6
 *
 * @author :            zhaohq
 * date :               2018/8/29 0029 14:12
 */
@Slf4j
@SuppressWarnings("unchecked")
public class CreditsCardGame {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        Pair<Integer, Integer>[] ts = new Pair[n];
        for (int i = 0; i < n; i++)
            ts[i] = Pair.of(sc.nextInt(), sc.nextInt());

        System.out.println(__maxGroupScore(ts));
        System.out.println(_maxGroupScore(ts));
        System.out.println(maxGroupScore(ts));
    }

    private static int __maxGroupScore(Pair<Integer, Integer>[] ts) {
        int sx = 0, sy = 0;
        for (Pair<Integer, Integer> t : ts) {
            sx += t.getLeft();
            sy += t.getRight();
        }
        int[][] f = new int[2][sx + 1];
        Arrays.fill(f[0], -sy - 1);
        Arrays.fill(f[1], -sy - 1);
        int[] pc = {0, 1};
        f[pc[1]][sx] = 0;
        for (Pair<Integer, Integer> t : ts) {
            ArrayUtil.swap(pc, 0, 1);
            for (int j = 0; j < f[0].length; j++) {
                f[pc[1]][j] = Math.max(f[pc[0]][j], f[pc[0]][Math.abs(j - t.getLeft())] + t.getRight());
                if (j + t.getLeft() < f[0].length)
                    f[pc[1]][j] = Math.max(f[pc[1]][j], f[pc[0]][j + t.getLeft()] + t.getRight());
            }
            log.debug("\n" + t + "\t\tf[0] = " + Arrays.toString(f[0]) + //
                    "\t\tf[1] = " + Arrays.toString(f[1]));
        }

        return f[pc[1]][sx];
    }

    private static int _maxGroupScore(Pair<Integer, Integer>[] ts) {
        int sx = 0, sy = 0;
        for (Pair<Integer, Integer> t : ts) {
            sx += t.getLeft();
            sy += t.getRight();
        }
        int[][] f = new int[2][2 * sx + 1];
        Arrays.fill(f[0], -sy - 1);
        Arrays.fill(f[1], -sy - 1);
        int[] pc = {0, 1};
        f[pc[1]][sx] = 0;
        for (Pair<Integer, Integer> t : ts) {
            ArrayUtil.swap(pc, 0, 1);
            for (int j = 0; j < f[0].length; j++) {
                f[pc[1]][j] = f[pc[0]][j];
                //log.debug("f[0][" + j + "] = " + f[0][j] + "\t\tf[1][" + j + "] = " + f[1][j]);
                if (j - t.getLeft() >= 0)
                    f[pc[1]][j] = Math.max(f[pc[1]][j], f[pc[0]][j - t.getLeft()] + t.getRight());
                //log.debug("f[0][" + j + "] = " + f[0][j] + "\t\tf[1][" + j + "] = " + f[1][j]);
                if (j + t.getLeft() < f[0].length)
                    f[pc[1]][j] = Math.max(f[pc[1]][j], f[pc[0]][j + t.getLeft()] + t.getRight());
                //log.debug("f[0][" + j + "] = " + f[0][j] + "\t\tf[1][" + j + "] = " + f[1][j]);
            }
            log.debug("\n" + t + "\t\tf[0] = " + Arrays.toString(f[0]) + //
                    "\t\tf[1] = " + Arrays.toString(f[1]));
        }

        return f[pc[1]][sx];
    }

    private static int maxGroupScore(Pair[] ts) {
        final int[] max = {0};
        ArrayUtil.coCombination(ts, t -> {
            List<Pair> a = t.getLeft();
            List<Pair> b = t.getRight();
            int s1 = a.stream().mapToInt(Pair<Integer, Integer>::getLeft).sum();
            int s2 = b.stream().mapToInt(Pair<Integer, Integer>::getLeft).sum();
            if (s1 == s2) {
                int gs = a.stream().mapToInt(Pair<Integer, Integer>::getRight).sum() + b.stream().mapToInt(Pair<Integer, Integer>::getRight).sum();
                log.debug("group and the corresponding score when a and b have the same individual score: "
                        + t.toString() + "\t" + gs);
                if (gs > max[0]) max[0] = gs;
            }
        });

        return max[0];
    }

}
