package cn.cstn.algorithm.offer;

/**
 * 统计一个数字在排序数组中出现的次数。
 * <p>
 * <p>
 * 示例 1:
 * <p>
 * 输入: nums = [5,7,7,8,8,10], target = 8
 * <p>
 * 输出: 2
 * <p>
 * <p>
 * 示例 2:
 * <p>
 * 输入: nums = [5,7,7,8,8,10], target = 6
 * <p>
 * 输出: 0
 * <p>  
 * <p>
 * 限制：
 * <p>
 * 0 <= 数组长度 <= 50000
 * <p>
 *
 * @author zhaohuiqiang
 * @date 2020/7/23 9:39
 */
public class CountTargetInSortedArr {

    public static void main(String[] args) {
        int[] nums = {5,7,7,8,8,10};
        System.out.println(search(nums, 8));
    }

    public static int search(int[] nums, int target) {
        int l = 0, r = nums.length - 1;
        while (l <= r) {
            int m = (l + r) / 2;
            if (nums[m] <= target) l = m + 1;
            else r = m - 1;
        }
        int right = l;
        if (right == 0 || nums[right - 1] != target) return 0;

        l = 0;
        r = nums.length - 1;
        while (l <= r) {
            int m = (l + r) / 2;
            if (nums[m] < target) l = m + 1;
            else r = m - 1;
        }
        int left = r;

        return right - left - 1;
    }

}
