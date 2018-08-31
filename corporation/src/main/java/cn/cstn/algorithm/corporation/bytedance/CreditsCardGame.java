package cn.cstn.algorithm.corporation.bytedance;

import cn.cstn.algorithm.commons.math.Point;
import cn.cstn.algorithm.commons.util.ArrayUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Scanner;

/**
 * 头条8.12             3.积分卡牌游戏
 * description :        小a和小b玩一个游戏，有n张卡牌，每张上面有两个正整数x,y。取一张牌时，
 *                      个人积分增加x，团队积分增加y。求小a，小b各取若干张牌，使得他们的个人积分相等，
 *                      且团队(小a、小b属于同一团队)积分最大。
 *                      输入描述：
 *                          第一行n, 接下来n行，每行两个整数x,y
 *                      输出描述：
 *                          一行一个整数,表示小a的积分和小b的积分相等的时候，团队积分的最大值。
 *                      输入:
 *                          4
 *                          3 1
 *                          2 2
 *                          1 4
 *                          1 4
 *                      输出: 10
 *                      数据范围:
 *                          0 < n < 100
 *                          0 < x < 1000
 *                          0 < y < 1e6
 * @author :           zhaohq
 * date :               2018/8/29 0029 14:12
 */
@Slf4j
public class CreditsCardGame {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        Point[] ps = new Point[n];
        for (int i = 0; i < n; i++)
            ps[i] = new Point(sc.nextInt(), sc.nextInt());

        System.out.println(solve(ps));
    }

    private static int solve(Point[] ps) {
        final int[] max = {0};
        ArrayUtil.coCombination(ps, t -> {
            List<Point> a = t._1();
            List<Point> b = t._2();
            int s1 = a.stream().mapToInt(Point::getX).sum();
            int s2 = b.stream().mapToInt(Point::getX).sum();
            if (s1 == s2) {
                int gs = a.stream().mapToInt(Point::getY).sum() + b.stream().mapToInt(Point::getY).sum();
                log.debug("group and the corresponding score when a and b have the same individual score: "
                        + t.toString() + "\t" + gs);
                if (gs > max[0]) max[0] = gs;
            }
        });

        return max[0];
    }

}
