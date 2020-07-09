package cn.cstn.algorithm.offer;

import cn.cstn.algorithm.commons.util.ArrayUtil;
import org.apache.commons.lang3.ArrayUtils;

import java.util.PriorityQueue;
import java.util.Queue;

/**
 * 输入整数数组 arr ，找出其中最小的 k 个数。例如，输入4、5、1、6、2、7、3、8这8个数字，则最小的4个数字是1、2、3、4。
 * <p>
 * <p>
 * 示例 1：
 * <p>
 * 输入：arr = [3,2,1], k = 2
 * <p>
 * 输出：[1,2] 或者 [2,1]
 * <p>
 * <p>
 * 示例 2：
 * <p>
 * 输入：arr = [0,1,2,1], k = 1
 * <p>
 * 输出：[0]
 * <p>  
 * <p>
 * 限制：
 * <p>
 * 0 <= k <= arr.length <= 10000
 * <p>
 * 0 <= arr[i] <= 10000
 * <p>
 *
 * @author zhaohuiqiang
 * @date 2020/7/9 15:27
 */
public class LeastNumbers {

    public static void main(String[] args) {
        int[] arr = {5, 4, 2, 3, 2, 1};
        int[] leastNumbers = getLeastNumbers(arr, 3);
        ArrayUtil.println(leastNumbers);
        leastNumbers = getLeastNumbers_(arr, 3);
        ArrayUtil.println(leastNumbers);
    }


    public static int[] getLeastNumbers(int[] arr, int k) {
        if (arr == null || k == 0) return new int[0];
        int p = ArrayUtil.indexOfKthMin(arr, k);
        return ArrayUtils.subarray(arr, 0, p + 1);
    }

    public static int[] getLeastNumbers_(int[] arr, int k) {
        Queue<Integer> queue = new PriorityQueue<>((x, y) -> y - x);
        for (int i = 0; i < k; i++) {
            queue.add(arr[i]);
        }
        for (int i = k; i < arr.length; i++) {
            if (!queue.isEmpty() && arr[i] < queue.peek()) {
                queue.poll();
                queue.add(arr[i]);
            }
        }

        int[] res = new int[k];
        int j = 0;
        while (!queue.isEmpty()) {
            res[j++] = queue.poll();
        }
        return res;
    }

}
