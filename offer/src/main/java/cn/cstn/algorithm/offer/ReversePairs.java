package cn.cstn.algorithm.offer;

import cn.cstn.algorithm.commons.util.ArrayUtil;

/**
 * 在数组中的两个数字，如果前面一个数字大于后面的数字，则这两个数字组成一个逆序对。输入一个数组，求出这个数组中的逆序对的总数。
 * <p>
 * <p>
 * 示例 1:
 * <p>
 * 输入: [7,5,6,4]
 * <p>
 * 输出: 5
 * <p>  
 * <p>
 * 限制：
 * <p>
 * 0 <= 数组长度 <= 50000
 * <p>
 *
 * @author zhaohuiqiang
 * @date 2020/7/21 16:42
 */
public class ReversePairs {

    public static void main(String[] args) {
        int[] nums = {7, 5, 6, 4};
        System.out.println(reversePairs(nums));
    }

    public static int reversePairs(int[] nums) {
        int[] tmp = new int[nums.length];
        return reversePairs(nums, 0, nums.length - 1, tmp);
    }

    private static int reversePairs(int[] nums, int l, int r, int[] tmp) {
        if (l >= r) return 0;

        int m = (l + r) / 2;
        int left = reversePairs(nums, l, m, tmp);
        int right = reversePairs(nums, m + 1, r, tmp);
        if (nums[m] <= nums[m + 1]) return left + right;

        int cross = crossReversePairs(nums, l, m, r, tmp);

        return left + cross + right;
    }

    private static int crossReversePairs(int[] nums, int l, int m, int r, int[] tmp) {
        System.arraycopy(nums, l, tmp, l, r - l + 1);

        int res = 0, i = l, j = m + 1;
        for (int k = l; k <= r; k++) {
            if (i == m + 1) {
                nums[k] = tmp[j];
                j++;
            } else if (j == r + 1) {
                nums[k] = tmp[i];
                i++;
            } else if (tmp[i] <= tmp[j]) {
                nums[k] = tmp[i];
                i++;
            } else {
                nums[k] = tmp[j];
                j++;
                res += m - i + 1;
            }
        }

        return res;
    }

    @SuppressWarnings("unused")
    private static int crossReversePairs(int[] nums, int l, int m, int r) {
        int res = 0;
        for (int i = l; i <= m; i++) {
            int j = m + 1;
            while (j <= r && nums[j] < nums[i]) j++;
            res += j - m - 1;
        }
        ArrayUtil.merge(nums, l, m, r);

        return res;
    }

}
