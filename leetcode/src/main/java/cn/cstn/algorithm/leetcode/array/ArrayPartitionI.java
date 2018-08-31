package cn.cstn.algorithm.leetcode.array;

import java.util.Arrays;

/**
 * 561                  Array Partition I
 * description :        Given an array of 2n integers, your task is to group these integers into n pairs of integer,
 *                      say (a1, b1), (a2, b2), ..., (an, bn) which makes sum of min(ai, bi) for all i from 1 to n
 *                      as large as possible.
 *                      Example 1:
 *                              Input: [1,4,3,2]
 *                              Output: 4
 *                              Explanation: n is 2, and the maximum sum of pairs is 4 = min(1, 2) + min(3, 4).
 *                      Note:
 *                              n is a positive integer, which is in the range of [1, 10000].
 *                              All the integers in the array will be in the range of [-10000, 10000].
 * @author :            zhaohq
 * date :               2018/8/12 0012 14:09
 */
public class ArrayPartitionI {
    public static void main(String[] args) {
        int[] nums = {1,4,3,2};
        System.out.println(arrayPairSum(nums));
    }

    private static int arrayPairSum(int[] nums) {
        if (nums == null || nums.length == 0) return 0;
        int res = 0;
        Arrays.sort(nums);
        for (int i = 0; i < nums.length - 1; i += 2)
            res += nums[i];

        return res;
    }
}
