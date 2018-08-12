package cn.cstn.algorithm.commons.util;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Consumer;

/**
 * description :        common sort algorithm
 * @author :           zhaohq
 * date :               2018/3/16 0012 21:39
 */
public class Sort {

    public static <T extends Comparable<T>> void insertSort(T[] arr) {
        int from = 0;
        insertSort(arr, from, arr.length);
    }

    private static <T extends Comparable<T>> void insertSort(T[] arr, int from, int to) {
        for (int i = from + 1; i < to; i++) {
            T x = arr[i];
            int j = i - 1;
            while (j >= from && x.compareTo(arr[j]) < 0) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = x;
        }
    }

    public static <T extends Comparable<T>> void selectSort(T[] arr) {
        int from = 0;
        selectSort(arr, from, arr.length);
    }

    private static <T extends Comparable<T>> void selectSort(T[] arr, int from, int to) {
        for (int i = from; i < to - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < to; j++)
                if (arr[j].compareTo(arr[minIndex]) < 0)
                    minIndex = j;
            if (minIndex != i)
                swap(arr, i, minIndex);
        }
    }

    public static <T extends Comparable<T>> void bubbleSort(T[] arr) {
        int from = 0;
        bubbleSort(arr, from, arr.length);
    }

    private static <T extends Comparable<T>> void bubbleSort(T[] arr, int from, int to) {
        for (int i = from; i < to - 1; i++)
            for (int j = to - 1; j > i; j--)
                if (arr[j].compareTo(arr[j - 1]) < 0)
                    swap(arr, j, j - 1);
    }

    public static <T extends Comparable<T>> void mergeSort(T[] arr) {
        int low = 0;
        mergeSort(arr, low, arr.length - 1);
    }

    private static <T extends Comparable<T>> void mergeSort(T[] arr, int low, int high) {
        if (low == high) return;
        int mid = (low + high) / 2;

        mergeSort(arr, low, mid);
        mergeSort(arr, mid + 1, high);
        merge(arr, low, mid, high);

    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> void merge(T[] arr, int low, int mid, int high) {
        T[] t = (T[]) Array.newInstance(arr[0].getClass(), high - low + 1);
        int i = low, j = mid + 1, k = 0;

        while (i <= mid && j <= high)
            if (arr[i].compareTo(arr[j]) < 0)
                t[k++] = arr[i++];
            else
                t[k++] = arr[j++];

        while (i <= mid)
            t[k++] = arr[i++];
        while (j <= high)
            t[k++] = arr[j++];

        System.arraycopy(t, 0, arr, low, high + 1 - low);

    }

    public static <T extends Comparable<T>> void quickSort(T[] arr) {
        quickSort(arr, 0, arr.length - 1);
    }

    private static <T extends Comparable<T>> void quickSort(T[] arr, int low, int high) {
        if (low >= high) return;
        int p = partition(arr, low, high);
        quickSort(arr, low, p - 1);
        quickSort(arr, p + 1, high);
    }

    private static <T extends Comparable<T>> int partition(T[] arr, int low, int high) {
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

    private static <T> void swap(T[] arr, int from, int to) {
        T t = arr[from];
        arr[from] = arr[to];
        arr[to] = t;
    }

    public static <T> void radixSort(T[] arr, int k, Character[] radix) {
        int from = 0;
        radixSort(arr, from, arr.length, k, radix);
    }

    @SuppressWarnings("unchecked")
    private static <T> void radixSort(T[] arr, int from, int to, int k, Character[] radix) {
        Map<Character, Integer> radix2Index = new HashMap<>();
        for (int i = 0; i < radix.length; i++)
            radix2Index.put(radix[i], i);

        for (int i = 0; i < k; i++) {
            List<T>[] lists = new ArrayList[radix.length];
            for (int j = from; j < to; j++) {
                Character ci;
                try {
                    ci = new StringBuilder(arr[j].toString()).reverse().charAt(i);
                } catch (Exception e) {
                    ci = radix[0];
                }
                Integer indexOfCi = radix2Index.get(ci);
                List<T> list = lists[indexOfCi];
                if (list == null)
                    list = lists[indexOfCi] = new ArrayList<>();
                list.add(arr[j]);
            }

            int j = 0;
            for (List<T> list : lists)
                if (list != null)
                    for (T item : list)
                        arr[j++] = item;
        }
    }

    public static void countSort(Integer[] arr, int bound) {
        int from = 0;
        countSort(arr, from, arr.length, bound);
    }

    private static void countSort(Integer[] arr, int from, int to, int bound) {
        Integer[] countArr = new Integer[bound];
        Arrays.fill(countArr, 0);
        for (Integer anArr : arr)
            countArr[anArr]++;

        int j = to - 1;
        for (int i = bound - 1; i >= 0; i--) {
            while (countArr[i] != 0 && j >= from) {
                arr[j--] = i;
                countArr[i]--;
            }
        }
    }

    public static <T extends Comparable<T>> void heapSort(T[] arr) {
        int from = 0;
        heapSort(arr, from, arr.length);
    }

    private static <T extends Comparable<T>> void heapSort(T[] arr, int from, int to) {
        for (int i = (to - 2) / 2; i >= from; i--)
            shift(arr, i, to - 1);

        for (int i = to - 1; i > from; i--) {
            swap(arr, i, from);
            shift(arr, from, i - 1);
        }

    }

    private static <T extends Comparable<T>> void shift(T[] arr, int i, int to) {
        int l = 2 * i + 1;
        int r = 2 * (i + 1);

        if (l > to || //
                (l == to && arr[i].compareTo(arr[l]) >= 0) || //
                (arr[i].compareTo(arr[l]) >= 0 && arr[i].compareTo(arr[r]) >= 0))
            return;

        if (r <= to && arr[r].compareTo(arr[l]) > 0) {
            swap(arr, i, r);
            shift(arr, r, to);
        } else {
            swap(arr, i, l);
            shift(arr, l, to);
        }
    }

    public static <T extends Comparable<T>> void bucketSort(T[] arr) {
        int from = 0;
        bucketSort(arr, from, arr.length);
    }

    private static <T extends Comparable<T>> void bucketSort(T[] arr, int from, int to) {
        Character[] radix = new Character[10];
        for (int i = 0; i < radix.length; i++)
            radix[i] = Character.forDigit(i, 10);

        bucketSort(arr, from, to, radix);

    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> void bucketSort(T[] arr, int from, int to, Character... radix) {
        Map<Character, Integer> radix2Index = new HashMap<>();
        List<T>[] buckets = new ArrayList[radix.length];
        for (int i = 0; i < radix.length; i++) {
            radix2Index.put(radix[i], i);
            buckets[i] = new ArrayList<>();
        }

        for (int i = from; i < to; i++) {
            Character initial = arr[i].toString().charAt(0);
            List<T> bucket = buckets[radix2Index.get(initial)];
            bucket.add(arr[i]);
        }

        int k = from;
        for (List<T> bucket : buckets) {
            for (T t : bucket) {
                arr[k++] = t;
            }
            insertSort(arr, 0, k);
        }
    }

    public static <T extends Comparable<T>> void sort(Consumer<T[]> consumer, T[] arr) {
        consumer.accept(arr);
    }

}
