package cn.cstn.algorithm.commons.test;

import cn.cstn.algorithm.commons.Search;
import org.junit.Test;

import java.util.Arrays;

public class SearchTest {

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
