package cn.cstn.algorithm.corporation.jd;

import cn.cstn.algorithm.commons.util.MathUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Scanner;

/**
 * 京东2018             求幂
 * description :        东东对幂运算很感兴趣,在学习的过程中东东发现了一些有趣的性质: 9^3 = 27^2, 2^10 = 32^2
 *                      东东对这个性质充满了好奇,东东现在给出一个整数n,希望你能帮助他求出满足 a^b = c^d
 *                      (1 ≤ a,b,c,d ≤ n)的式子有多少个。
 *                      例如当n = 2: 1^1=1^1
 *                                   1^1=1^2
 *                                   1^2=1^1
 *                                   1^2=1^2
 *                                   2^1=2^1
 *                                   2^2=2^2
 *                      一共有6个满足要求的式子
 *                      输入描述:
 *                         输入包括一个整数n(1 ≤ n ≤ 10^6)
 *                      输出描述:
 *                         输出一个整数,表示满足要求的式子个数。因为答案可能很大,输出对1000000007求模的结果
 *                      输入例子1: 2
 *                      输出例子1: 6
 * @author :            zhaohq
 * date :               2018-09-08 16:20
 */
@Slf4j
public class Exponentiation {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();

        System.out.println(numOfExpression(n));
    }

    private static int numOfExpression(int n) {
        int num = n * n + n * (n - 1);
        for (int i = 2; i < n; i++) {
            int login = (int) (Math.log(n) / Math.log(i));
            log.debug("\nlog" + i + "(" + n + ") = Math.log(" + n + ") / Math.log(" + i + ") : " +//
                    Math.log(n) / Math.log(i) + " = " + Math.log(n) + " / " + Math.log(i));
            for (int j = 1; j <= login; j++)
                for (int k = 1; k <= login; k++)
                    if (j != k) num += n / (Math.max(j, k) / MathUtil.gcd(j, k));
        }

        return num;
    }

}
