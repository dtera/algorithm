package cn.cstn.algorithm.leetcode.array;

import cn.cstn.algorithm.leetcode.util.ArrayHelper;

import java.util.Arrays;

/**
 * 832               Flipping an Image
 * description :     Given a binary matrix A, we want to flip the image horizontally, then invert it,
 *                   and return the resulting image.
 *                   To flip an image horizontally means that each row of the image is reversed.
 *                   For example, flipping [1, 1, 0] horizontally results in [0, 1, 1].
 *                   To invert an image means that each 0 is replaced by 1, and each 1 is replaced by 0.
 *                   For example, inverting [0, 1, 1] results in [1, 0, 0].
 * example 1:
 *                   Input: [[1,1,0],[1,0,1],[0,0,0]]
 *                   Output: [[1,0,0],[0,1,0],[1,1,1]]
 *                   Explanation: First reverse each row: [[0,1,1],[1,0,1],[0,0,0]].
 *                   Then, invert the image: [[1,0,0],[0,1,0],[1,1,1]]
 * example 2:
 *                   Input: [[1,1,0,0],[1,0,0,1],[0,1,1,1],[1,0,1,0]]
 *                   Output: [[1,1,0,0],[0,1,1,0],[0,0,0,1],[1,0,1,0]]
 *                   Explanation: First reverse each row: [[0,0,1,1],[1,0,0,1],[1,1,1,0],[0,1,0,1]].
 *                   Then invert the image: [[1,1,0,0],[0,1,1,0],[0,0,0,1],[1,0,1,0]]
 * notes:
 *                   1 <= A.length = A[0].length <= 20
 *                   0 <= A[i][j] <= 1
 * @author :        zhaohq
 * date :            2018-07-26 18:54
 */
public class FlipAndInvertImage {
    public static void main(String[] args) {
        int[][] A = {{1, 1, 0}, {1, 0, 1}, {0, 0, 0}};
        int[][] fii = flipAndInvertImage(A);
        Arrays.asList(fii).forEach(ArrayHelper::println);
        System.out.println("======================================");
        fii = _flipAndInvertImage(A);
        Arrays.asList(fii).forEach(ArrayHelper::println);
        /*Integer[][] B = {{1, 1, 0}, {1, 0, 1}, {0, 0, 0}};
        Stream.of(B).map(Arrays::asList).forEach(System.out::println);*/

    }

    private static int[][] _flipAndInvertImage(int[][] A) {
        for (int[] a : A)
            for (int i = 0; i < (a.length + 1) / 2; i++)
                flipAndInvertSwap(a, i, a.length - 1 - i);

        return A;
    }

    private static int[][] flipAndInvertImage(int[][] A) {
        int[][] B = new int[A.length][];
        for (int i = 0; i < A.length; i++) {
            B[i] = new int[A[0].length];
            for (int j = 0; j < A[0].length; j++)
                B[i][j] = A[i][A[0].length - 1 - j] ^ 1;
        }

        return B;
    }

    private static void flipAndInvertSwap(int[] a, int i, int j) {
        int t = a[i];
        a[i] = a[j] ^ 1;
        a[j] = t ^ 1;
    }

}
