package cn.cstn.algorithm.corporation.jd;

import java.util.Scanner;

/**
 * 京东2018          疯狂序列
 * description :     东东从京京那里了解到有一个无限长的数字序列: 1, 2, 2, 3, 3, 3, 4, 4, 4, 4,
 *                   5, 5, 5, 5, 5, ...(数字k在该序列中正好出现k次)。东东想知道这个数字序列的第n项是多少,
 *                   你能帮帮他么
 *                   输入描述:
 *                      输入包括一个整数n(1 ≤ n ≤ 10^18)
 *                   输出描述:
 *                      输出一个整数,即数字序列的第n项
 *                   输入例子1: 169
 *                   输出例子1: 18
 * @author :         zhaohq
 * date :            2018-09-08 16:15
 */
public class CrazySequence {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int k = (int) Math.sqrt(2 * n);
        if (n <= k * (k - 1) / 2 || n > k * (k + 1) / 2) k++;
        System.out.println(k);
    }
}
