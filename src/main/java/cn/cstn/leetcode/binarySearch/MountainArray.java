package cn.cstn.leetcode.binarySearch;

/**
 * 852               Peak Index in a Mountain Array
 * description :     Let's call an array A a mountain if the following properties hold:
 * A.length >= 3
 * There exists some 0 < i < A.length - 1 such that
 * A[0] < A[1] < ... A[i-1] < A[i] > A[i+1] > ... > A[A.length - 1]
 * Given an array that is definitely a mountain, return any i such that
 * A[0] < A[1] < ... A[i-1] < A[i] > A[i+1] > ... > A[A.length - 1].
 * example 1 :
 * Input: [0,1,0]
 * Output: 1
 * example 2 :
 * Input: [0,2,1,0]
 * Output: 1
 * note:
 * 3 <= A.length <= 10000
 * 0 <= A[i] <= 10^6
 * A is a mountain, as defined above.
 *
 * @author :        zhaohq
 * date :            2018-07-26 20:04
 */
public class MountainArray {
    public static void main(String[] args) {
        int[] A = {0, 1, 2, 3, 5, 4, 2, 0};
        System.out.println(peakIndexInMountainArray(A));
        System.out.println(_peakIndexInMountainArray(A));
    }

    private static int _peakIndexInMountainArray(int[] A) {
        if (A.length < 3) throw new IllegalStateException("A.length must >=3");
        int left = 0, right = A.length - 1;
        while (left < right) {
            int mid = (left + right) / 2;
            if (A[mid] < A[mid + 1]) left = mid + 1;
            else right = mid;
        }
        return left;
    }

    private static int peakIndexInMountainArray(int[] A) {
        if (A.length < 3) throw new IllegalStateException("A.length must >=3");
        return peakIndexInMountainArray(A, 0, A.length - 1);
    }

    private static int peakIndexInMountainArray(int[] A, int from, int to) {
        int mid = (from + to) / 2;
        if (A[mid] > A[mid - 1] && A[mid] > A[mid + 1]) return mid;
        if (A[mid] > A[mid - 1] && A[mid] < A[mid + 1])
            return peakIndexInMountainArray(A, mid + 1, to);

        return peakIndexInMountainArray(A, from, mid - 1);
    }
}
