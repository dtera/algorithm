package cn.cstn.algorithm.commons.util;

import cn.cstn.algorithm.commons.KV;
import cn.cstn.algorithm.commons.Tuple;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Consumer;

/**
 * description :     ArrayUtil
 * @author :         zhaohq
 * date :            2018-07-27 16:31
 */
@Slf4j
public class ArrayUtil {

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
        int i  = mi;
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
            if (arr[j].compareTo(arr[high]) <= 0) {
                i++;
                swap(arr, i, j);
            }
            j++;
        }
        swap(arr, i + 1, high);

        return i + 1;
    }

    public static <T> void reverse(T[] a) {
        reverse(a, 0, a.length - 1);
    }

    public static <T> void reverse(T[] a, int from, int to) {
        while (from < to) {
            T t = a[from];
            a[from++] = a[to];
            a[to--] = t;
        }
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
