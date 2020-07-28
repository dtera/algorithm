package cn.cstn.algorithm.offer;

/**
 * 一个长度为n-1的递增排序数组中的所有数字都是唯一的，并且每个数字都在范围0～n-1之内。
 * 在范围0～n-1内的n个数字中有且只有一个数字不在该数组中，请找出这个数字。
 * <p>
 * <p>
 * 示例 1:
 * <p>
 * 输入: [0,1,3]
 * <p>
 * 输出: 2
 * <p>
 * <p>
 * 示例 2:
 * <p>
 * 输入: [0,1,2,3,4,5,6,7,9]
 * <p>
 * 输出: 8
 * <p>  
 * <p>
 * 限制：
 * <p>
 * 1 <= 数组长度 <= 10000
 * <p>
 *
 * @author zhaohuiqiang
 * @date 2020/7/28 9:22
 */
public class MissingNumber {

    public static void main(String[] args) {
        int[] nums = {0, 1, 2, 3, 4, 5, 6, 7, 9};
        System.out.println(missingNumber(nums));
    }

    public static int missingNumber(int[] nums) {
        int l = 0, r = nums.length - 1;
        while (l <= r) {
            int m = (l + r) / 2;
            if (nums[m] == m) l = m + 1;
            else r = m - 1;
        }

        return l;
    }

}
