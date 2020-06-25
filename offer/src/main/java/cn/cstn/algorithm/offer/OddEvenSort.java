package cn.cstn.algorithm.offer;

import cn.cstn.algorithm.commons.util.ArrayUtil;

/**
 * 输入一个整数数组，实现一个函数来调整该数组中数字的顺序，使得所有奇数位于数组的前半部分，所有偶数位于数组的后半部分。
 * <p>
 * <p>
 * 示例：
 * <p>
 * 输入：nums = [1,2,3,4]
 * <p>
 * 输出：[1,3,2,4]
 * <p>
 * 注：[3,1,2,4] 也是正确的答案之一。
 *  
 * <p>
 * 提示：
 * <p>
 * 1 <= nums.length <= 50000
 * <p>
 * 1 <= nums[i] <= 10000
 *
 * @author zhaohuiqiang
 * @date 2020/6/25 20:29
 */
public class OddEvenSort {

    public static void main(String[] args) {
        int[] nums = {1, 2, 3, 4, 5, 6, 7, 8};
        ArrayUtil.println(exchange(nums));
    }

    public static int[] exchange(int[] nums) {
        int i = -1, j = 0;
        while (j < nums.length) {
            if (nums[j] % 2 == 1) {
                i++;
                ArrayUtil.swap(nums, i, j);
            }
            j++;
        }
        return nums;
    }

}
