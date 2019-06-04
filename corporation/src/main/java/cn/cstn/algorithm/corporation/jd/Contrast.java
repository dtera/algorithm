package cn.cstn.algorithm.corporation.jd;

import org.apache.commons.lang3.tuple.Triple;

import java.util.Scanner;

/**
 * 京东2018.9.9         2.对比
 * description :        现有n个物品，每个物品有3个参数ai，bi，ci，定义i物品不合格的依据是：若存在物品j，
 *                      且aj>ai，bj>bi，cj>ci，则称物品i为不合格品。
 *                      现给出n个物品的a，b，c参数，请你求出不合格品的数量。
 *                      输入：
 *                          第一行包含一个整数n（1<=n<=500000）,表示物品的数量，接下来有n行，每行有3个
 *                          整数，ai，bi，ci表示第i个物品的3个参数，1<=ai，bi，ci<=10^9。
 *                      输出：
 *                          输出包含一个整数，表示不合格品的数量。
 * @author :            zhaohq
 * date :               2018-09-10 10:04
 */
@SuppressWarnings("unchecked")
public class Contrast {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        Triple<Integer, Integer, Integer>[] ts = new Triple[n];
        for (int i = 0; i < n; i++)
            ts[i] = Triple.of(sc.nextInt(), sc.nextInt(), sc.nextInt());

        System.out.println(numOfQualifiedProduct(ts));
    }

    private static int numOfQualifiedProduct(Triple<Integer, Integer, Integer>[] ts) {
        int num = 0, n = ts.length;
        boolean[] valid = new boolean[n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                if (!valid[j] && i != j && ts[i].getLeft() < ts[j].getLeft()&&//
                        ts[i].getMiddle() < ts[j].getMiddle()&& ts[i].getRight() < ts[j].getLeft()) {
                    valid[i] = true;
                    num++;
                    break;
                }

        return num;
    }

}
