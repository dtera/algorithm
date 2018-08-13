package cn.cstn.algorithm.commons;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

public class App {

    @Test
    public void sort() {
        Map<String, Consumer<Integer[]>> sorts = new HashMap<>();
        sorts.put("插入", Sort::insertSort);
        sorts.put("选择", Sort::selectSort);
        sorts.put("冒泡", Sort::bubbleSort);
        sorts.put("归并", Sort::mergeSort);
        sorts.put("快速", Sort::quickSort);
        sorts.put("基数", App::radixSort);
        sorts.put("计数", App::countSort);
        sorts.put("堆", Sort::heapSort);
        sorts.put("桶", Sort::bucketSort);/**/

        Integer[] origin = new Integer[100000];
        Random rd = new Random();
        for (int i = 0; i < 100000; i++) {
            origin[i] = rd.nextInt(100000);
        }
        //System.out.println( "排序前：    " + Arrays.asList(new Integer[]{92, 63, 42, 35, 74, 13, 89, 6, 48, 46, 23, 72, 17, 54}));
        //System.out.println( "排序前：    " + Arrays.asList(origin));
        System.out.println("===================================================================");
        for (String type : sorts.keySet()) {
            //Integer[] arr = {92, 63, 42, 35, 74, 13, 89, 6, 48, 46, 23, 72, 17, 54};
            Integer[] arr = origin.clone();
            System.out.println(type + "排序之前：" + Arrays.asList(arr).subList(0, 100));
            long begin = System.currentTimeMillis();
            Sort.sort(sorts.get(type), arr);
            long end = System.currentTimeMillis();
            System.out.println(type + "排序之后：" + Arrays.asList(arr).subList(0, 100));
            System.out.println(type + "排序耗时：" + (end - begin) + "ms");
            System.out.println("===================================================================");
        }

    }

    private static void countSort(Integer[] arr) {
        int bound = 100000;
        Sort.countSort(arr, bound);
    }

    private static  <T> void radixSort(T[] arr) {
        Character[] radix = new Character[10];
        for (int i = 0; i < radix.length; i++)
            radix[i] = Character.forDigit(i, 10);

        Sort.radixSort(arr, 5, radix);
    }

    @Test
    public void find() {
        Integer[] arr = {5, 6, 7, 8, 9, 1, 2, 3, 4};
        System.out.println("带查找数组：" + Arrays.asList(arr));
        //Arrays.stream(arr).forEach(p->System.out.print(p+", "));

        System.out.println("================分治查找旋转数组================");
        for (int a : arr)
            System.out.println(a + "元素的索引为：" + Search.rotatedArrFind(arr, a));

        Arrays.sort(arr);
        System.out.println("================二分查找有序数组================");
        for (int a : arr)
            System.out.println(a + "元素的索引为：" + Search.BS(arr, a));

        System.out.println(arr[0] + "元素的索引为：" + Search.BS(arr, arr[0], 0, arr.length));
    }


}
