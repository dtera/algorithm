package cn.cstn.algorithm.offer;

import cn.cstn.algorithm.commons.util.ArrayUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 输入数字 n，按顺序打印出从 1 到最大的 n 位十进制数。比如输入 3，则打印出 1、2、3 一直到最大的 3 位数 999。
 * <p>
 * <p>
 * 示例 1:
 * <p>
 * 输入: n = 1
 * <p>
 * 输出: [1,2,3,4,5,6,7,8,9]
 *  
 * <p>
 * 说明：
 * <p>
 * 用返回一个整数列表来代替打印
 * <p>
 * n 为正整数
 *
 * @author zhaohuiqiang
 * @date 2020/6/22 9:23
 */
public class PrintNumbers {

    public static void main(String[] args) {
        int[] numbers = _printNumbers(1);
        ArrayUtil.println(numbers);
        List<String> nums = printNumbers(2);
        System.out.println(nums);
    }

    public static List<String> printNumbers(int n) {
        List<String> res = new ArrayList<>();
        char[] num = new char[n];
        for (int i = 0; i < n; i++) {
            num[i] = '0';
        }

        while (!increasable(num)) {
            int i = 0;
            while (i < n && num[i] == '0') i++;
            res.add(String.valueOf(num, i, n - i));
        }

        return res;
    }

    private static boolean increasable(char[] num) {
        boolean isOverflow = false;
        for (int i = num.length - 1; i >= 0; i--) {
            char s = (char) (num[i] + 1);
            if (s > '9') {
                if (i == 0) {
                    isOverflow = true;
                }
                num[i] = '0';
            } else {
                num[i] = s;
                break;
            }
        }
        return isOverflow;
    }

    public static int[] _printNumbers(int n) {
        int len = (int) (Math.pow(10, n) - 1);
        int[] res = new int[len];

        for (int i = 0; i < len; i++) {
            res[i] = i + 1;
        }

        return res;
    }

}
