package cn.cstn.algorithm.commons.util;

/**
 * description :     ArrayUtil
 * @author :        zhaohq
 * date :            2018-07-27 16:31
 */
public class ArrayUtil {
    public static int[] indexOfMinMax(int[] a) {
        int[] imm = new int[2];
        for (int i = 0; i < a.length; i++) {
            if (a[i] < a[imm[0]]) imm[0] = i;
            if (a[i] > a[imm[1]]) imm[1] = i;
        }

        return imm;
    }

    public static int kthMin(int[] a, int k) {
        int[] b = new int[a.length];
        System.arraycopy(a, 0, b, 0, a.length);
        int ki = partition(b, 0, b.length - 1);
        while (ki != k - 1) {
            if (ki < k - 1) ki = partition(b, ki + 1, b.length - 1);
            if (ki > k - 1) ki = partition(b, 0, ki - 1);
        }

        return b[ki];
    }

    private static int partition(int[] arr, int low, int high) {
        int j = low, i = low - 1;
        while (j < high) {
            if (arr[j] < arr[high]) {
                i++;
                swap(arr, i, j);
            }
            j++;
        }
        swap(arr, i + 1, high);

        return i + 1;
    }

    private static void swap(int[] arr, int i, int j) {
        int t = arr[i];
        arr[i] = arr[j];
        arr[j] = t;
    }

    public static <T extends Comparable<T>> int partition(T[] arr, int low, int high) {
        int j = low, i = low - 1;
        while (j < high) {
            if (arr[j].compareTo(arr[high]) <= 0) {
                i++;
                swap(arr, i, j);
            }
            j++;
        }
        swap(arr, i + 1, high);

        return i + 1;
    }

    public static <T> void swap(T[] arr, int i, int j) {
        T t = arr[i];
        arr[i] = arr[j];
        arr[j] = t;
    }

    public static int[][] getWalkDirections() {
        return new int[][]{{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};
    }

    public static void println(int[] a) {
        print(a, ", ", "[", "]\n");
    }

    public static void print(int[] a, String sep, String s, String e) {
        if (a == null) return;
        System.out.print(s);
        for (int i = 0; i < a.length - 1; i++)
            System.out.print(a[i] + sep);

        System.out.print(a[a.length - 1] + e);
    }

    public static <T> void println(T[] a) {
        print(a, ", ", "[", "]\n");
    }

    public static <T> void print(T[] a, String sep, String s, String e) {
        if (a == null) return;
        System.out.print(s);
        for (int i = 0; i < a.length - 1; i++)
            System.out.print(a[i] + sep);

        System.out.print(a[a.length - 1] + e);
    }

}