package cn.cstn.algorithm.offer;

/**
 * 数组中有一个数字出现的次数超过数组长度的一半，请找出这个数字。
 * <p>
 * <p>
 * 你可以假设数组是非空的，并且给定的数组总是存在多数元素。
 * <p>
 * <p>
 * 示例 1:
 * <p>
 * 输入: [1, 2, 3, 2, 2, 2, 5, 4, 2]
 * <p>
 * 输出: 2
 * <p>  
 * <p>
 * 限制：
 * <p>
 * 1 <= 数组长度 <= 50000
 * <p>
 *
 * @author zhaohuiqiang
 * @date 2020/7/9 14:45
 */
public class MajorityElement {

    public static void main(String[] args) {
        int[] nums = {1, 2, 3, 2, 2, 2, 5, 4, 2};
        System.out.println(majorityElement(nums));
    }

    public static int majorityElement(int[] nums) {
        int res = 0, vote = 0;
        for (int num : nums) {
            if (vote == 0) res = num;
            vote += res == num ? 1 : -1;
        }
        return res;
    }

}
