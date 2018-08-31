package cn.cstn.algorithm.commons.test;

import cn.cstn.algorithm.commons.math.Point;
import cn.cstn.algorithm.commons.util.ArrayUtil;
import cn.cstn.algorithm.commons.util.CollectionUtil;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ArrayTest {

    @Test
    public void testPrint() {
        int[] a = {4, 6, 1, 8, 5, 3, 9, 7, 2};
        Integer[] b = {4, 6, 1, 8, 5, 3, 9, 7, 2};
        ArrayUtil.println(a);
        ArrayUtil.print(a, ", ", "", "\n");
        ArrayUtil.println(b);
        ArrayUtil.print(b, ", ", "", "\n");
        ArrayUtil.swap(a, 1, 2);
    }

    @Test
    public void testReverse() {
        Integer[] a = {1, 2, 3};
        ArrayUtil.reverse(a);
        ArrayUtil.println(a);
        System.out.println("========================================");
        ArrayUtil.reverse(a, 1, 2);
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
        Point[] ps = {new Point(1, 3), new Point(2, 1), new Point(3, 2)};
        ArrayUtil.println(ps);
        System.out.println("next permutation:");
        while (ArrayUtil.nextPermutation(ps, Comparator.comparingInt(Point::getX), ArrayUtil::println))
            System.out.println("next permutation:");
    }

    @Test
    public void testCombination() {
        Integer[] a = {1, 2, 3, 4};
        ArrayUtil.combination(a, System.out::println);
        System.out.println("=============================");
        ArrayUtil.combination(a, 3, System.out::println);
    }

    @Test
    public void testCoCombination() {
        Integer[] a = {1, 2, 3, 4};
        ArrayUtil.coCombination(a, System.out::println);
    }

}
