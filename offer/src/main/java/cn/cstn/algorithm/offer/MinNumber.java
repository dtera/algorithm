package cn.cstn.algorithm.offer;

import cn.cstn.algorithm.commons.util.ArrayUtil;

import java.util.Arrays;

/**
 * 输入一个非负整数数组，把数组里所有数字拼接起来排成一个数，打印能拼接出的所有数字中最小的一个。
 * <p>
 * <p>
 * 示例 1:
 * <p>
 * 输入: [10,2]
 * <p>
 * 输出: "102"
 * <p>
 * <p>
 * 示例 2:
 * <p>
 * 输入: [3,30,34,5,9]
 * <p>
 * 输出: "3033459"
 * <p>  
 * <p>
 * 提示:
 * <p>
 * 0 < nums.length <= 100
 * <p>
 * <p>
 * 说明:
 * <p>
 * 输出结果可能非常大，所以你需要返回一个字符串而不是整数
 * <p>
 * 拼接起来的数字可能会有前导 0，最后结果不需要去掉前导 0
 * <p>
 *
 * @author zhaohuiqiang
 * @date 2020/7/10 18:12
 */
public class MinNumber {

    public static void main(String[] args) {
        int[] nums = {3, 30, 34, 5, 9};
        System.out.println(minNumber(nums));
    }

    public static String minNumber(int[] nums) {
        ArrayUtil.quickSort(nums, (x, y) -> (int)(Long.parseLong(x + "" + y) - Long.parseLong(y + "" + x)));
        StringBuilder res = new StringBuilder();
        Arrays.stream(nums).forEach(res::append);
        return res.toString();
    }

}
