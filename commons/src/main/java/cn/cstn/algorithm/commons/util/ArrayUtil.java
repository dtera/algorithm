package cn.cstn.algorithm.commons.util;

import cn.cstn.algorithm.commons.KV;
import cn.cstn.algorithm.commons.Tuple;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Consumer;

/**
 * description :     ArrayUtil
 *
 * @author :         zhaohq
 * date :            2018-07-27 16:31
 */
@Slf4j
public class ArrayUtil {

    public static String kthItemOfMirroredArr(int n, int k) {
        int len = (int) Math.pow(2, n);
        if (n < 1 || k < 0 || k >= len) return "not exists";
        if (n == 1) return "" + k;
        else {
            if (k < len / 2) return "0" + kthItemOfMirroredArr(n - 1, k);
            else return "1" + kthItemOfMirroredArr(n - 1, len - 1 - k);
        }
    }

    public static <T> int longestPalindrome(T[] a) {
        if (a == null || a.length == 0) return 0;
        int ml = 1, n = a.length;
        for (int i = 0; i < n; i++) {
            int j = 1;
            while (i - j >= 0 && i + j < n && a[i - j].equals(a[i + j])) j++;
            ml = Math.max(ml, 2 * (j - 1) + 1);
            j = 0;
            while (i - j >= 0 && i + j + 1 < n && a[i - j].equals(a[i + j + 1])) j++;
            ml = Math.max(ml, 2 * j);
        }

        return ml;
    }

    public static boolean isMultiStageGraph(Set<Integer>[] g) {
        int n = g.length;
        boolean[] access = new boolean[n];
        for (int i = 0; i < n; i++) {
            log.debug("g[" + i + "] = {" + i + " -> " + g[i].toString() + "}");
            if (access[i]) continue;
            access[i] = true;
            for (int j = 0; j < n; j++)
                if (!access[j] && !g[i].contains(j))
                    if (g[i].equals(g[j]))
                        access[j] = true;
                    else return false;
        }

        return true;
    }

    public static <T extends Comparable<T>> KV<Integer, LinkedList<T>> lis(T[] a) {
        return lis(a, Comparable<T>::compareTo);
    }

    public static <T extends Comparable<T>> KV<Integer, LinkedList<T>> lis(T[] a, int t) {
        return lis(a, t, Comparable<T>::compareTo);
    }

    public static <T> KV<Integer, LinkedList<T>> lis(T[] a, Comparator<T> comparator) {
        return lis(a, 1, comparator);
    }

    public static <T> KV<Integer, LinkedList<T>> lis(T[] a, int t, Comparator<T> comparator) {
        int n = a.length, mi = 0;
        int[] dp = new int[n * t];
        int[] dpi = new int[n * t];
        LinkedList<T> seq = new LinkedList<>();

        for (int i = 0; i < n * t; i++) {
            int k = -1, cm = 0;
            for (int j = 0; j < i; j++)
                if (comparator.compare(a[j % n], a[i % n]) < 0 && dp[j] > cm) {
                    cm = dp[j];
                    k = j;
                }
            dp[i] = cm + 1;
            dpi[i] = k;
            if (dp[i] > dp[mi]) mi = i;
        }
        int i = mi;
        for (; dpi[i] != -1; i = dpi[i]) seq.addFirst(a[i % n]);
        seq.addFirst(a[i % n]);

        log.debug("a = " + Arrays.toString(a));
        log.debug("dp = " + Arrays.toString(dp));
        log.debug("dpi = " + Arrays.toString(dpi));
        log.debug("seq = " + seq);

        return new KV<>(dp[mi], seq);
    }

    public static <T> void coCombination(T[] a, Consumer<Tuple<List<T>>> consumer) {
        Map<Integer, List<List<T>>> map = new HashMap<>();
        combination(a, 1, a.length - 1, list -> {
            List<List<T>> lists = map.computeIfAbsent(list.size(), k -> new ArrayList<>());
            lists.add(list);
        });

        for (int i = 1; i <= a.length / 2; i++) {
            List<List<T>> pls = map.get(i);
            CollectionUtil.combinationPair(pls, t -> {
                List<T> intersect = CollectionUtil.intersect(t._1(), t._2());
                if (intersect.size() == 0) consumer.accept(t);
            });
            for (int j = i + 1; j < a.length && i + j <= a.length; j++) {
                List<List<T>> cls = map.get(j);
                CollectionUtil.combinationPair(pls, cls, t -> {
                    List<T> intersect = CollectionUtil.intersect(t._1(), t._2());
                    if (intersect.size() == 0) consumer.accept(t);
                });
            }
        }
    }

    public static <T> void combination(T[] a, Consumer<List<T>> consumer) {
        combination(a, 1, a.length, consumer);
    }

    public static <T> void combination(T[] a, int from, int to, Consumer<List<T>> consumer) {
        assert from > 0 && to <= a.length;
        for (int i = from; i <= to; i++)
            combination(a, i, consumer);
    }

    public static <T> void combination(T[] a, int n, Consumer<List<T>> consumer) {
        combination(a, 0, n, new ArrayList<>(), consumer);
    }

    private static <T> void combination(T[] a, int from, int n, List<T> pl, Consumer<List<T>> consumer) {
        if (n < 0 || n > a.length - from || consumer == null) return;
        if (n == 0) consumer.accept(pl);
        if (n == 1)
            for (int i = from; i < a.length; i++) {
                List<T> cl = new ArrayList<>(pl);
                cl.add(a[i]);
                consumer.accept(cl);
            }
        else if (n == a.length - from) {
            List<T> cl = new ArrayList<>(pl);
            cl.addAll(Arrays.asList(a).subList(from, a.length));
            consumer.accept(cl);
        } else {
            T t = a[from];
            List<T> cl = new ArrayList<>(pl);
            cl.add(t);
            combination(a, from + 1, n - 1, cl, consumer);
            combination(a, from + 1, n, pl, consumer);
        }
    }

    public static <T extends Comparable<T>> boolean nextPermutation(T[] a, Consumer<T[]> consumer) {
        return nextPermutation(a, Comparable::compareTo, consumer);
    }

    public static <T> boolean nextPermutation(T[] a, Comparator<T> comparator, Consumer<T[]> consumer) {
        int n = a.length, i, j;
        for (i = n - 1; i > 0; i--)
            if (comparator.compare(a[i], a[i - 1]) > 0) break;
        if (i == 0)
            return false;

        for (j = n - 1; j > i; j--)
            if (comparator.compare(a[j], a[i - 1]) > 0) break;
        swap(a, i - 1, j);
        reverse(a, i, n - 1);
        consumer.accept(a);

        return true;
    }

    public static <T> void permutation(T[] a, Consumer<T[]> consumer) {
        permutation(a, 0, a.length - 1, consumer);
    }

    public static <T> void permutation(T[] a, int from, int to, Consumer<T[]> consumer) {
        if (from == to) {
            consumer.accept(a);
            return;
        }

        permutation(a, from + 1, to, consumer);
        for (int i = from + 1; i <= to; i++) {
            swap(a, from, i);
            permutation(a, from + 1, to, consumer);
            swap(a, from, i);
        }
    }

    public static List<Integer> asList(int... a) {
        List<Integer> res = new ArrayList<>();
        for (int ai : a)
            res.add(ai);

        return res;
    }

    public static int[] indexOfMinMax(int[] a) {
        return indexOfMinMax(a, 0, a.length - 1);
    }

    public static int[] indexOfMinMax(int[] a, int from, int to) {
        int[] imm = new int[2];
        imm[0] = from;
        imm[1] = from;
        for (int i = from + 1; i <= to; i++) {
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

    public static <T extends Comparable<T>> int partition(T[] arr, int low, int high) {
        int j = low, i = low - 1;
        while (j < high) {
            if (arr[j].compareTo(arr[high]) < 0) {
                i++;
                swap(arr, i, j);
            }
            j++;
        }
        swap(arr, i + 1, high);

        return i + 1;
    }

    public static String reverse(String s) {
        char[] a = s.toCharArray();
        reverse(a, 0, s.length() - 1);
        return new String(a);
    }

    public static void reverse(char[] a, int from, int to) {
        while (from < to)
            swap(a, from++, to--);
    }

    public static <T> void reverse(T[] a) {
        reverse(a, 0, a.length - 1);
    }

    public static <T> void reverse(T[] a, int from, int to) {
        while (from < to)
            swap(a, from++, to--);
    }

    public static void swap(char[] arr, int i, int j) {
        char t = arr[i];
        arr[i] = arr[j];
        arr[j] = t;
    }

    public static void swap(int[] arr, int i, int j) {
        int t = arr[i];
        arr[i] = arr[j];
        arr[j] = t;
    }

    public static <T> void swap(T[] arr, int i, int j) {
        T t = arr[i];
        arr[i] = arr[j];
        arr[j] = t;
    }

    public static int[][] getWalkDirections() {
        return new int[][]{{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};
    }

    public static Integer[] primitiveToObj(int[] a) {
        Integer[] b = new Integer[a.length];
        for (int i = 0; i < a.length; i++)
            b[i] = a[i];

        return b;
    }

    public static void println(int[] a) {
        print(a, ", ", "[", "]\n");
    }

    public static <T> void println(T[] a) {
        print(a, ", ", "[", "]\n");
    }

    public static void print(int[] a, String sep, String s, String e) {
        print(primitiveToObj(a), sep, s, e);
    }

    public static <T> void print(T[] a, String sep, String s, String e) {
        if (a == null) return;
        System.out.print(s);
        for (int i = 0; i < a.length - 1; i++)
            System.out.print(a[i] + sep);

        System.out.print(a[a.length - 1] + e);
    }

    //=================================common sort algorithm================================================
    public static <T extends Comparable<T>> void insertSort(T[] arr) {
        int begin = 0;
        insertSort(arr, begin, arr.length);
    }

    private static <T extends Comparable<T>> void insertSort(T[] arr, int begin, int end) {
        for (int i = begin + 1; i < end; i++) {
            T x = arr[i];
            int j = i - 1;
            while (j >= begin && x.compareTo(arr[j]) < 0) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = x;
        }
    }

    public static <T extends Comparable<T>> void selectSort(T[] arr) {
        int begin = 0;
        selectSort(arr, begin, arr.length);
    }

    private static <T extends Comparable<T>> void selectSort(T[] arr, int begin, int end) {
        for (int i = begin; i < end - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < end; j++)
                if (arr[j].compareTo(arr[minIndex]) < 0)
                    minIndex = j;
            if (minIndex != i)
                swap(arr, i, minIndex);
        }
    }

    public static <T extends Comparable<T>> void bubbleSort(T[] arr) {
        int begin = 0;
        bubbleSort(arr, begin, arr.length);
    }

    private static <T extends Comparable<T>> void bubbleSort(T[] arr, int begin, int end) {
        for (int i = begin; i < end - 1; i++)
            for (int j = end - 1; j > i; j--)
                if (arr[j].compareTo(arr[j - 1]) < 0)
                    swap(arr, j, j - 1);
    }

    public static <T extends Comparable<T>> void mergeSort(T[] arr) {
        int from = 0;
        mergeSort(arr, from, arr.length - 1);
    }

    private static <T extends Comparable<T>> void mergeSort(T[] arr, int from, int to) {
        if (from == to) return;
        int mid = (from + to) / 2;

        mergeSort(arr, from, mid);
        mergeSort(arr, mid + 1, to);
        merge(arr, from, mid, to);

    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> void merge(T[] arr, int from, int mid, int to) {
        T[] t = (T[]) Array.newInstance(arr[0].getClass(), to - from + 1);
        int i = from, j = mid + 1, k = 0;

        while (i <= mid && j <= to)
            if (arr[i].compareTo(arr[j]) < 0)
                t[k++] = arr[i++];
            else
                t[k++] = arr[j++];

        while (i <= mid)
            t[k++] = arr[i++];
        while (j <= to)
            t[k++] = arr[j++];

        System.arraycopy(t, 0, arr, from, to - from + 1);

    }

    public static <T extends Comparable<T>> void quickSort(T[] arr) {
        quickSort(arr, 0, arr.length - 1);
    }

    private static <T extends Comparable<T>> void quickSort(T[] arr, int from, int to) {
        if (from >= to) return;
        int p = partition(arr, from, to);
        quickSort(arr, from, p - 1);
        quickSort(arr, p + 1, to);
    }

    public static <T> void radixSort(T[] arr, int k, Character[] radix) {
        int begin = 0;
        radixSort(arr, begin, arr.length, k, radix);
    }

    @SuppressWarnings("unchecked")
    private static <T> void radixSort(T[] arr, int begin, int end, int k, Character[] radix) {
        Map<Character, Integer> radix2Index = new HashMap<>();
        for (int i = 0; i < radix.length; i++)
            radix2Index.put(radix[i], i);

        String[] a = new String[end - begin];
        for (int i = begin; i < end; i++)
            a[i] = reverse(arr[i].toString());

        for (int i = 0; i < k; i++) {
            List<T>[] lists = new ArrayList[radix.length];
            for (int j = begin; j < end; j++) {
                Character ci;
                try {
                    ci = a[j].charAt(i);
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
        int begin = 0;
        countSort(arr, begin, arr.length, bound);
    }

    private static void countSort(Integer[] arr, int begin, int end, int bound) {
        Integer[] countArr = new Integer[bound];
        Arrays.fill(countArr, 0);
        for (int i = begin; i < end; i++)
            countArr[arr[i]]++;

        int j = end - 1;
        for (int i = bound - 1; i >= 0; i--) {
            while (countArr[i] != 0 && j >= begin) {
                arr[j--] = i;
                countArr[i]--;
            }
        }
    }

    public static <T extends Comparable<T>> void heapSort(T[] arr) {
        int begin = 0;
        heapSort(arr, begin, arr.length);
    }

    private static <T extends Comparable<T>> void heapSort(T[] arr, int begin, int end) {
        for (int i = (end - 2) / 2; i >= begin; i--)
            shift(arr, i, end - 1);

        for (int i = end - 1; i > begin; i--) {
            swap(arr, i, begin);
            shift(arr, begin, i - 1);
        }

    }

    private static <T extends Comparable<T>> void shift(T[] arr, int i, int end) {
        int l = 2 * i + 1;
        int r = 2 * (i + 1);

        if (l > end || //
                (l == end && arr[i].compareTo(arr[l]) >= 0) || //
                (arr[i].compareTo(arr[l]) >= 0 && arr[i].compareTo(arr[r]) >= 0))
            return;

        if (r <= end && arr[r].compareTo(arr[l]) > 0) {
            swap(arr, i, r);
            shift(arr, r, end);
        } else {
            swap(arr, i, l);
            shift(arr, l, end);
        }
    }

    public static <T extends Comparable<T>> void bucketSort(T[] arr) {
        int begin = 0;
        bucketSort(arr, begin, arr.length);
    }

    private static <T extends Comparable<T>> void bucketSort(T[] arr, int begin, int end) {
        Character[] radix = new Character[10];
        for (int i = 0; i < radix.length; i++)
            radix[i] = Character.forDigit(i, 10);

        bucketSort(arr, begin, end, radix);

    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> void bucketSort(T[] arr, int begin, int end, Character... radix) {
        Map<Character, Integer> radix2Index = new HashMap<>();
        List<T>[] buckets = new ArrayList[radix.length];
        for (int i = 0; i < radix.length; i++) {
            radix2Index.put(radix[i], i);
            buckets[i] = new ArrayList<>();
        }

        for (int i = begin; i < end; i++) {
            Character initial = arr[i].toString().charAt(0);
            List<T> bucket = buckets[radix2Index.get(initial)];
            bucket.add(arr[i]);
        }

        int k = begin;
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
    //=================================common sort algorithm================================================

    //=================================search algorithm=====================================================
    public static <T extends Comparable<T>> int rotatedArrFind(T[] arr, T target) {
        int low = 0;
        int high = arr.length - 1;

        while (low <= high) {
            if (target.compareTo(arr[low]) == 0) return low;
            final int mid = (low + high) / 2;
            if (target.compareTo(arr[mid]) == 0) return mid;

            //method1:
            if (arr[mid].compareTo(arr[low]) > 0)
                if (target.compareTo(arr[low]) >= 0 && target.compareTo(arr[mid]) < 0)
                    return BS(arr, target, low, mid - 1);
                else
                    low = mid + 1;
            else if (arr[mid].compareTo(arr[low]) < 0) {
                if (target.compareTo(arr[mid]) > 0 && target.compareTo(arr[high]) <= 0)
                    return BS(arr, target, mid + 1, high);
                else
                    high = mid - 1;
            } else low++;

            //method2:
            /*else if (target < arr[mid]) {
                if (arr[mid] > arr[low])
                    if (target >= arr[low])
                        return BS(arr, target, low, mid - 1);
                    else
                        low = mid + 1;
                else high = mid - 1;
            } else {
                if (arr[mid] < arr[high])
                    if (target <= arr[high])
                        return BS(arr, target, mid + 1, high);
                    else
                        high = mid - 1;
                else low = mid + 1;
            }*/

        }

        return -1;
    }

    public static <T extends Comparable<T>> int BS(T[] arr, T target) {
        return BS(arr, target, 0, arr.length - 1);
    }

    public static <T extends Comparable<T>> int BS(T[] arr, T target, int low, int high) {
        int mid = (low + high) / 2;
        while (low <= high) {
            if (arr[mid].compareTo(target) == 0) return mid;
            else if (target.compareTo(arr[mid]) < 0)
                high = mid - 1;
            else
                low = mid + 1;
            mid = (low + high) / 2;
        }
        return -1;
    }
    //=================================search algorithm=====================================================

}
