package cn.cstn.algorithm.offer;

import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;

/**
 * 找出数组中重复的数字。
 * <p>
 * <p>
 * 在一个长度为 n 的数组 nums 里的所有数字都在 0～n-1 的范围内。数组中某些数字是重复的，但不知道有几个数字重复了，
 * 也不知道每个数字重复了几次。请找出数组中任意一个重复的数字。
 * <p>
 * 示例 1：
 * <p>
 * 输入：
 * [2, 3, 1, 0, 2, 5, 3]
 * <p>
 * 输出：2 或 3 
 * <p>
 * 限制：2 <= n <= 100000
 */
@Slf4j
public class FindRepeatNumber {

    public static void main(String... args) {
        int[] nums = {2, 3, 1, 0, 3, 2, 5, 3};
        System.out.println(_findRepeatNumber(nums));
        System.out.println(findRepeatNumber(nums));
    }

    public static int findRepeatNumber(int[] nums) {
        log.debug("Integer.MAX_VALUE={}", Integer.MAX_VALUE);
        log.debug("Integer.MIN_VALUE={}", Integer.MIN_VALUE);
        for (int num : nums) {
            log.debug("nums={}", nums);
            int index = num < 0 ? num + Integer.MAX_VALUE + 1 : num;
            if (nums[index] < 0) {
                return index;
            }
            nums[index] = nums[index] - Integer.MAX_VALUE - 1;
        }
        return -1;
    }

    public static int _findRepeatNumber(int[] nums) {
        HashSet<Integer> set = new HashSet<>();
        int res = -1;
        for (int num : nums) {
            if (!set.add(num)) {
                res = num;
                break;
            }
        }
        return res;
    }

}

