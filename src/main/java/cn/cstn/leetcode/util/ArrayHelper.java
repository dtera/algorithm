package cn.cstn.leetcode.util;

/**
 * description :     ArrayHelper
 * @author :        zhaohq
 * date :            2018-07-27 16:31
 */
public class ArrayHelper {
    public static void println(int[] a) {
        System.out.print("[");
        for (int i = 0; i < a.length - 1; i++)
            System.out.print(a[i] + ", ");

        System.out.print(a[a.length - 1] + "]\n");
    }

}
