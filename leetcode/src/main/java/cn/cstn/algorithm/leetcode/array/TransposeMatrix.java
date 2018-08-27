package cn.cstn.algorithm.leetcode.array;

import cn.cstn.algorithm.commons.util.ArrayUtil;

import java.util.Arrays;

/**
 * 867               Transpose Matrix
 * description :     Given a matrix A, return the transpose of A.
 *                   The transpose of a matrix is the matrix flipped over it's main diagonal,
 *                   switching the row and column indices of the matrix.
 * example 1:
 *                   Input: [[1,2,3],[4,5,6],[7,8,9]]
 *                   Output: [[1,4,7],[2,5,8],[3,6,9]]
 * example 2:
 *                   Input: [[1,2,3],[4,5,6]]
 *                   Output: [[1,4],[2,5],[3,6]]
 * note :
 *                   1 <= A.length <= 1000
 *                   1 <= A[0].length <= 1000
 * @author :        zhaohq
 * date :            2018-07-27 16:19
 */
public class TransposeMatrix {
    public static void main(String[] args) {
        int[][] A = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        Arrays.asList(A).forEach(ArrayUtil::println);
        System.out.println();
        Arrays.asList(transpose(A)).forEach(ArrayUtil::println);
    }

    private static int[][] transpose(int[][] A) {
        int[][] tA = new int[A[0].length][A.length];
        for (int i = 0; i < A.length; i++)
            for (int j = 0; j < A[0].length; j++)
                tA[j][i] = A[i][j];

        return tA;
    }
}
