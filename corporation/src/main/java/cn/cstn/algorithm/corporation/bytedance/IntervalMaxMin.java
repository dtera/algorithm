package cn.cstn.algorithm.corporation.bytedance;

import cn.cstn.algorithm.commons.util.ArrayUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

/**
 * 头条8.12             4.区间最大最小值
 * description :        两个长度为n的序列a,b,问有多少个区间[l,r]满足max(a[l,r]) < min(b[l,r]),
 *                      即a区间的最大值小于b区间的最小值
 *                      数据范围：
 *                          n < 1e5
 *                          ai,bi < 1e9
 *                      输入描述：
 *                          第一行一个整数n,第二行n个数，第i个为ai,第三行n个数，第i个为bi
 *                          0 <= l <= r < n
 *                      输出描述：
 *                          一行一个整数，表示答案
 *                      输入：
 *                          3
 *                          3 2 1
 *                          3 3 3
 *                      输出：  3
 * @author :            zhaohq
 * date :               2018/8/29 0029 14:20
 */
@Slf4j
public class IntervalMaxMin {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int[] a = new int[n];
        int[] b = new int[n];
        for (int i = 0; i < n; i++) a[i] = sc.nextInt();
        for (int i = 0; i < n; i++) b[i] = sc.nextInt();

        System.out.println(numOfInterval(a, b));
    }

    private static int numOfInterval(int[] a, int[] b) {
        int num = 0;
        log.debug("\na: " + ArrayUtil.asList(a));
        log.debug("\nb: " + ArrayUtil.asList(b));
        for (int i = 0; i < a.length; i++)
            for (int j = i; j < b.length; j++) {
                int[] ia = ArrayUtil.indexOfMinMax(a, i, j);
                int[] ib = ArrayUtil.indexOfMinMax(b, i, j);
                log.debug("\nia: " + ArrayUtil.asList(ia));
                log.debug("\nib: " + ArrayUtil.asList(ib));
                if (a[ia[1]] < b[ib[0]]) num++;
            }

        return num;
    }
}
