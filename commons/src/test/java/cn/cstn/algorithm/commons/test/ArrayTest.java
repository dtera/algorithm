package cn.cstn.algorithm.commons.test;

import cn.cstn.algorithm.commons.Tuple;
import cn.cstn.algorithm.commons.util.ArrayUtil;
import cn.cstn.algorithm.commons.util.StringUtil;
import org.junit.Test;

import java.util.*;
import java.util.function.Consumer;

public class ArrayTest {

    @Test
    public void testPrint() {
        int[] a = {4, 6, 1, 8, 5, 3, 9, 7, 2};
        Integer[] b = {4, 6, 1, 8, 5, 3, 9, 7, 2};
        ArrayUtil.println(ArrayUtil.primitiveToObj(a));
        ArrayUtil.print(a, ", ", "", "\n");
        ArrayUtil.println(b);
        ArrayUtil.print(b, ", ", "", "\n");
        ArrayUtil.swap(a, 1, 2);
        ArrayUtil.swap(b, 1, 2);
    }

    @Test
    public void testReverse() {
        Integer[] a = {1, 2, 3};
        char[] c = {'a', 'b', 'c'};
        String s = "zhaohq";
        ArrayUtil.reverse(s);
        ArrayUtil.reverse(a);
        ArrayUtil.println(a);
        System.out.println("========================================");
        ArrayUtil.reverse(a, 1, 2);
        ArrayUtil.reverse(c, 1, 2);
        ArrayUtil.swap(c, 1, 2);
        ArrayUtil.println(a);
    }

    @Test
    public void testPermutation() {
        Integer[] a = {1, 2, 3};
        ArrayUtil.permutation(a, ArrayUtil::println);
        System.out.println("========================================");
        ArrayUtil.permutation(a, 1, 2, ArrayUtil::println);
        System.out.println("========================================");
        Integer[] b = {1, 2, 3};
        ArrayUtil.println(b);
        System.out.println("next permutation:");
        while (ArrayUtil.nextPermutation(b, ArrayUtil::println)) System.out.println("next permutation:");
        System.out.println("========================================");
        Tuple[] ts = {new Tuple<>(1, 3), new Tuple<>(2, 1), new Tuple<>(3, 2)};
        ArrayUtil.println(ts);
        System.out.println("next permutation:");
        while (ArrayUtil.nextPermutation(ts, Comparator.comparingInt(Tuple<Integer>::_1), ArrayUtil::println))
            System.out.println("next permutation:");
    }

    @Test
    public void testCombination() {
        Integer[] a = {1, 2, 3, 4};
        ArrayUtil.combination(a, System.out::println);
        System.out.println("=============================");
        ArrayUtil.combination(a, 3, System.out::println);
        System.out.println("=============================");
        ArrayUtil.combination(a, 1, 2, System.out::println);
    }

    @Test
    public void testCoCombination() {
        Integer[] a = {1, 2, 3, 4};
        ArrayUtil.coCombination(a, System.out::println);
    }

    @Test
    public void testLIS() {
        Integer[] a = {2, 1, 3, 1};
        Tuple[] b = {new Tuple<>(2, 6), new Tuple<>(1, 7), new Tuple<>(3, 8), new Tuple<>(1, 9)};
        System.out.println(ArrayUtil.lis(a));
        System.out.println(ArrayUtil.lis(a, 3));
        System.out.println(ArrayUtil.lis(b, Comparator.comparingInt(Tuple<Integer>::_1)));
        System.out.println(ArrayUtil.lis(b, 2, Comparator.comparingInt(Tuple<Integer>::_1)));
    }

    @Test
    public void testLongestPalindrome() {
        Integer[] a = {2, 2, 4, 5};
        System.out.println(ArrayUtil.longestPalindrome(a));
        System.out.println(StringUtil.longestPalindrome("google"));
    }

    @Test
    public void testKthItemOfMirroredArr() {
        System.out.println(ArrayUtil.kthItemOfMirroredArr(3, 3));
    }

    @Test
    public void sort() {
        Map<String, Consumer<Integer[]>> sorts = new HashMap<>();
        sorts.put("插入", ArrayUtil::insertSort);
        sorts.put("选择", ArrayUtil::selectSort);
        sorts.put("冒泡", ArrayUtil::bubbleSort);
        sorts.put("归并", ArrayUtil::mergeSort);
        sorts.put("快速", ArrayUtil::quickSort);
        sorts.put("基数", ArrayTest::radixSort);
        sorts.put("计数", ArrayTest::countSort);
        sorts.put("堆", ArrayUtil::heapSort);
        sorts.put("桶", ArrayUtil::bucketSort);/**/

        Integer[] origin = new Integer[100000];
        Random rd = new Random();
        for (int i = 0; i < 100000; i++) {
            origin[i] = rd.nextInt(100000);
        }
        //System.out.println( "排序前：    " + Arrays.asList(new Integer[]{92, 63, 42, 35, 74, 13, 89, 6, 48, 46, 23, 72, 17, 54}));
        //System.out.println( "排序前：    " + Arrays.asList(origin));
        System.out.println("===================================================================");
        for (String type : sorts.keySet()) {
            Integer[] arr = origin.clone();
            System.out.println(type + "排序之前：" + Arrays.asList(arr).subList(0, 100));
            long begin = System.currentTimeMillis();
            ArrayUtil.sort(sorts.get(type), arr);
            long end = System.currentTimeMillis();
            System.out.println(type + "排序之后：" + Arrays.asList(arr).subList(0, 100));
            System.out.println(type + "排序耗时：" + (end - begin) + "ms");
            System.out.println("===================================================================");
        }
        Integer[] arr = {92, 63, 42, 35, 74, 13, 89, 6, 48, 46, 23, 72, 17, 54};
        ArrayUtil.partition(arr, 0, arr.length - 1);

    }

    private static void countSort(Integer[] arr) {
        int bound = 100000;
        ArrayUtil.countSort(arr, bound);
    }

    private static  <T> void radixSort(T[] arr) {
        Character[] radix = new Character[10];
        for (int i = 0; i < radix.length; i++)
            radix[i] = Character.forDigit(i, 10);

        ArrayUtil.radixSort(arr, 5, radix);
    }

    @Test
    public void find() {
        Integer[] arr = {5, 5, 5, 6, 7, 8, 9, 1, 2, 3, 4};
        System.out.println("带查找数组：" + Arrays.asList(arr));
        //Arrays.stream(arr).forEach(p->System.out.print(p+", "));

        System.out.println("================分治查找旋转数组================");
        for (int a : new HashSet<>(Arrays.asList(arr)))
            System.out.println(a + "元素的索引为：" + ArrayUtil.rotatedArrFind(arr, a));

        Arrays.sort(arr);
        System.out.println("================二分查找有序数组================");
        for (int a : new HashSet<>(Arrays.asList(arr)))
            System.out.println(a + "元素的索引为：" + ArrayUtil.BS(arr, a));

        System.out.println(arr[0] + "元素的索引为：" + ArrayUtil.BS(arr, arr[0], 0, arr.length));
    }

}
