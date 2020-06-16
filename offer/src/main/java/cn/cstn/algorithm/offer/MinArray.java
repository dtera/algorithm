package cn.cstn.algorithm.offer;

/**
 * 把一个数组最开始的若干个元素搬到数组的末尾，我们称之为数组的旋转。输入一个递增排序的数组的一个旋转，输出旋转数组的最小元素。
 * 例如，数组 [3,4,5,1,2] 为 [1,2,3,4,5] 的一个旋转，该数组的最小值为1。  
 * <p>
 * <p>
 * 示例 1：
 * <p>
 * 输入：[3,4,5,1,2]
 * <p>
 * 输出：1
 * <p>
 * <p>
 * 示例 2：
 * <p>
 * 输入：[2,2,2,0,1]
 * <p>
 * 输出：0
 *
 * @author zhaohuiqiang
 * @date 2020/6/15 9:27
 */
public class MinArray {

    public static void main(String[] args) {
        int[] numbers = {2,2,2,0,1};
        System.out.println(minArray(numbers));
    }

    public static int minArray(int[] numbers) {
        if (numbers == null || numbers.length == 0) return -1;
        int i = 0, j = numbers.length - 1;
        while (i < j) {
            int m = (i + j) / 2;
            if (numbers[m] > numbers[j]) i = m + 1;
            else if (numbers[m] < numbers[j]) j = m;
            else j--;
        }
        return numbers[i];
    }

}
