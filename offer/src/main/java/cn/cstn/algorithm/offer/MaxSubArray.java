package cn.cstn.algorithm.offer;

import cn.cstn.algorithm.commons.util.ArrayUtil;

/**
 * 输入一个整型数组，数组里有正数也有负数。数组中的一个或连续多个整数组成一个子数组。求所有子数组的和的最大值。
 * <p>
 * <p>
 * 要求时间复杂度为O(n)。
 * <p>
 * <p>
 * 示例1:
 * <p>
 * 输入: nums = [-2,1,-3,4,-1,2,1,-5,4]
 * <p>
 * 输出: 6
 * <p>
 * <p>
 * 解释: 连续子数组 [4,-1,2,1] 的和最大，为 6。
 * <p>  
 * <p>
 * 提示：
 * <p>
 * 1 <= arr.length <= 10^5
 * <p>
 * -100 <= arr[i] <= 100
 * <p>
 *
 * @author zhaohuiqiang
 * @date 2020/7/10 10:41
 */
public class MaxSubArray {

    public static void main(String[] args) {
        int[] nums = {-2, 1, -3, 4, -1, 2, 1, -5, 4};
        System.out.println(ArrayUtil.maxSubArray(nums));
    }

}
