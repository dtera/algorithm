package cn.cstn.algorithm.commons.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

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
@SuppressWarnings("unused")
public class ArrayUtil {

    public static int maxSubArray(int[] nums) {
        /*int[] dp = new int[nums.length];
        int res = dp[0] = nums[0];
        for (int i = 1; i < nums.length; i++) {
            dp[i] = Math.max(dp[i - 1], 0) + nums[i];
            if (res < dp[i]) res = dp[i];
        }
        return res;*/

        int res = nums[0], curMax = nums[0];
        for (int i = 1; i < nums.length; i++) {
            curMax = Math.max(curMax, 0) + nums[i];
            if (res < curMax) res = curMax;
        }
        return res;
    }

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

    public static <T extends Comparable<T>> Pair<Integer, LinkedList<T>> lis(T[] a) {
        return lis(a, Comparable<T>::compareTo);
    }

    public static <T extends Comparable<T>> Pair<Integer, LinkedList<T>> lis(T[] a, int t) {
        return lis(a, t, Comparable<T>::compareTo);
    }

    public static <T> Pair<Integer, LinkedList<T>> lis(T[] a, Comparator<T> comparator) {
        return lis(a, 1, comparator);
    }

    public static <T> Pair<Integer, LinkedList<T>> lis(T[] a, int t, Comparator<T> comparator) {
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

        return Pair.of(dp[mi], seq);
    }

    public static <T> void coCombination(T[] a, Consumer<Pair<List<T>, List<T>>> consumer) {
        Map<Integer, List<List<T>>> map = new HashMap<>();
        combination(a, 1, a.length - 1, list -> {
            List<List<T>> lists = map.computeIfAbsent(list.size(), k -> new ArrayList<>());
            lists.add(list);
        });

        for (int i = 1; i <= a.length / 2; i++) {
            List<List<T>> pls = map.get(i);
            CollectionUtil.combinationPair(pls, t -> {
                List<T> intersect = CollectionUtil.intersect(t.getLeft(), t.getRight());
                if (intersect.size() == 0) consumer.accept(t);
            });
            for (int j = i + 1; j < a.length && i + j <= a.length; j++) {
                List<List<T>> cls = map.get(j);
                CollectionUtil.combinationPair(pls, cls, t -> {
                    List<T> intersect = CollectionUtil.intersect(t.getLeft(), t.getRight());
                    if (intersect.size() == 0) consumer.accept(t);
                });
            }
        }
    }

    public static <T> void combination(T[] a, Consumer<List<T>> consumer) {
        combination(a, 1, a.length, consumer);
    }

    public static <T> void combination(T[] a, int l, int r, Consumer<List<T>> consumer) {
        assert l > 0 && r <= a.length;
        for (int i = l; i <= r; i++)
            combination(a, i, consumer);
    }

    public static <T> void combination(T[] a, int n, Consumer<List<T>> consumer) {
        combination(a, 0, n, new ArrayList<>(), consumer);
    }

    private static <T> void combination(T[] a, int l, int n, List<T> pl, Consumer<List<T>> consumer) {
        if (n < 0 || n > a.length - l || consumer == null) return;
        if (n == 0) consumer.accept(pl);
        if (n == 1)
            for (int i = l; i < a.length; i++) {
                List<T> cl = new ArrayList<>(pl);
                cl.add(a[i]);
                consumer.accept(cl);
            }
        else if (n == a.length - l) {
            List<T> cl = new ArrayList<>(pl);
            cl.addAll(Arrays.asList(a).subList(l, a.length));
            consumer.accept(cl);
        } else {
            T t = a[l];
            List<T> cl = new ArrayList<>(pl);
            cl.add(t);
            combination(a, l + 1, n - 1, cl, consumer);
            combination(a, l + 1, n, pl, consumer);
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

    public static <T> void permutation(T[] a, int l, int r, Consumer<T[]> consumer) {
        if (l == r) {
            consumer.accept(a);
            return;
        }

        Set<T> visited = new HashSet<>();
        for (int i = l; i <= r; i++) {
            if (visited.contains(a[i])) continue;
            swap(a, l, i);
            permutation(a, l + 1, r, consumer);
            swap(a, l, i);
            visited.add(a[i]);
        }
    }

    public static List<Integer> asList(int... a) {
        List<Integer> res = new ArrayList<>();
        for (int ai : a)
            res.add(ai);

        return res;
    }

    public static List<Character> asList(char... a) {
        List<Character> res = new ArrayList<>();
        for (char ai : a)
            res.add(ai);

        return res;
    }

    public static int[] indexOfMinMax(int[] a) {
        return indexOfMinMax(a, 0, a.length - 1);
    }

    public static int[] indexOfMinMax(int[] a, int l, int r) {
        return indexOfMinMax(ArrayUtils.toObject(a), l, r);
    }

    public static int[] indexOfMinMax(Integer[] a) {
        return indexOfMinMax(a, 0, a.length - 1);
    }

    public static int[] indexOfMinMax(Integer[] a, int l, int r) {
        int[] imm = new int[2];
        imm[0] = l;
        imm[1] = l;
        for (int i = l + 1; i <= r; i++) {
            if (a[i] < a[imm[0]]) imm[0] = i;
            if (a[i] > a[imm[1]]) imm[1] = i;
        }

        return imm;
    }

    public static int kthMin(int[] a, int k) {
        return a[indexOfKthMin(a, k)];
    }

    @SuppressWarnings("DuplicatedCode")
    public static int indexOfKthMin(int[] a, int k) {
        int ki = randomizedPartition(a, 0, a.length - 1);
        while (ki != k - 1) {
            if (ki < k - 1) ki = randomizedPartition(a, ki + 1, a.length - 1);
            if (ki > k - 1) ki = randomizedPartition(a, 0, ki - 1);
        }

        return ki;
    }

    public static int kthMin(Integer[] a, int k) {
        return a[indexOfKthMin(a, k)];
    }

    @SuppressWarnings("DuplicatedCode")
    public static int indexOfKthMin(Integer[] a, int k) {
        int ki = randomizedPartition(a, 0, a.length - 1);
        while (ki != k - 1) {
            if (ki < k - 1) ki = randomizedPartition(a, ki + 1, a.length - 1);
            if (ki > k - 1) ki = randomizedPartition(a, 0, ki - 1);
        }

        return ki;
    }

    public static int randomizedPartition(int[] arr, int l, int r) {
        int i = (int) (Math.random() * (r - l + 1) + l);
        swap(arr, i, r);
        return partition(arr, l, r);
    }

    public static int partition(int[] arr, int l, int r) {
        int j = l, i = l - 1;
        while (j < r) {
            if (arr[j] < arr[r]) {
                i++;
                swap(arr, i, j);
            }
            j++;
        }
        swap(arr, i + 1, r);

        return i + 1;
    }

    public static <T extends Comparable<T>> int partition(T[] arr, int l, int r) {
        return partition(arr, l, r, Comparable<T>::compareTo);
    }

    public static <T> int partition(T[] arr, int l, int r, Comparator<T> comparator) {
        int j = l, i = l - 1;
        while (j < r) {
            if (comparator.compare(arr[j], arr[r]) < 0) {
                i++;
                swap(arr, i, j);
            }
            j++;
        }
        swap(arr, i + 1, r);

        return i + 1;
    }

    public static <T extends Comparable<T>> int randomizedPartition(T[] arr, int l, int r) {
        return randomizedPartition(arr, l, r, Comparable<T>::compareTo);
    }

    public static <T> int randomizedPartition(T[] arr, int l, int r, Comparator<T> comparator) {
        int i = (int) (Math.random() * (r - l + 1) + l);
        swap(arr, i, r);
        return partition(arr, l, r, comparator);
    }

    public static String reverse(String s) {
        char[] a = s.toCharArray();
        ArrayUtils.reverse(a);
        return new String(a);
    }

    public static <T> void reverse(T[] a) {
        reverse(a, 0, a.length - 1);
    }

    public static <T> void reverse(T[] a, int l, int r) {
        while (l < r)
            swap(a, l++, r--);
    }

    public static <T> void swap(T[] arr, int i, int j) {
        T t = arr[i];
        arr[i] = arr[j];
        arr[j] = t;
    }

    public static void swap(int[] arr, int i, int j) {
        int t = arr[i];
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

    public static Character[] primitiveToObj(char[] a) {
        Character[] b = new Character[a.length];
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
        int l = 0;
        mergeSort(arr, l, arr.length - 1);
    }

    private static <T extends Comparable<T>> void mergeSort(T[] arr, int l, int r) {
        if (l == r) return;
        int mid = (l + r) / 2;

        mergeSort(arr, l, mid);
        mergeSort(arr, mid + 1, r);
        merge(arr, l, mid, r);

    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> void merge(T[] arr, int l, int mid, int r) {
        T[] t = (T[]) Array.newInstance(arr[0].getClass(), r - l + 1);
        int i = l, j = mid + 1, k = 0;

        while (i <= mid && j <= r)
            if (arr[i].compareTo(arr[j]) < 0)
                t[k++] = arr[i++];
            else
                t[k++] = arr[j++];

        while (i <= mid)
            t[k++] = arr[i++];
        while (j <= r)
            t[k++] = arr[j++];

        System.arraycopy(t, 0, arr, l, r - l + 1);

    }

    public static <T extends Comparable<T>> void quickSort(T[] arr) {
        quickSort(arr, 0, arr.length - 1, Comparable<T>::compareTo);
    }

    public static <T> void quickSort(T[] arr, Comparator<T> comparator) {
        quickSort(arr, 0, arr.length - 1, comparator);
    }

    private static <T> void quickSort(T[] arr, int l, int r, Comparator<T> comparator) {
        if (l >= r) return;
        int p = partition(arr, l, r, comparator);
        quickSort(arr, l, p - 1, comparator);
        quickSort(arr, p + 1, r, comparator);
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
        int l = 0;
        int r = arr.length - 1;

        while (l <= r) {
            if (target.compareTo(arr[l]) == 0) return l;
            final int mid = (l + r) / 2;
            if (target.compareTo(arr[mid]) == 0) return mid;

            //method1:
            if (arr[mid].compareTo(arr[l]) > 0)
                if (target.compareTo(arr[l]) >= 0 && target.compareTo(arr[mid]) < 0)
                    return BS(arr, target, l, mid - 1);
                else
                    l = mid + 1;
            else if (arr[mid].compareTo(arr[l]) < 0) {
                if (target.compareTo(arr[mid]) > 0 && target.compareTo(arr[r]) <= 0)
                    return BS(arr, target, mid + 1, r);
                else
                    r = mid - 1;
            } else l++;

            //method2:
            /*else if (target < arr[mid]) {
                if (arr[mid] > arr[l])
                    if (target >= arr[l])
                        return BS(arr, target, l, mid - 1);
                    else
                        l = mid + 1;
                else r = mid - 1;
            } else {
                if (arr[mid] < arr[r])
                    if (target <= arr[r])
                        return BS(arr, target, mid + 1, r);
                    else
                        r = mid - 1;
                else l = mid + 1;
            }*/

        }

        return -1;
    }

    public static <T extends Comparable<T>> int BS(T[] arr, T target) {
        return BS(arr, target, 0, arr.length - 1);
    }

    public static <T extends Comparable<T>> int BS(T[] arr, T target, int l, int r) {
        int mid = (l + r) / 2;
        while (l <= r) {
            if (arr[mid].compareTo(target) == 0) return mid;
            else if (target.compareTo(arr[mid]) < 0)
                r = mid - 1;
            else
                l = mid + 1;
            mid = (l + r) / 2;
        }
        return -1;
    }
    //=================================search algorithm=====================================================

}
