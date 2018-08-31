package cn.cstn.algorithm.commons.test;

import cn.cstn.algorithm.commons.util.ArrayUtil;
import cn.cstn.algorithm.commons.util.CollectionUtil;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CollectionTest {
    @Test
    public void testPrint() {
        List<Integer> list = Arrays.asList(4, 6, 1, 8, 5, 3, 9, 7, 2);
        CollectionUtil.println(list);
        CollectionUtil.print(list, "; ", "", "\n");
        List<Integer[]> ls = Arrays.asList(new Integer[]{4, 6},new Integer[]{1, 8, 5, 3, 9, 7, 2});
        CollectionUtil.print(ls, "; ", "", "\n", this::print);
    }

    private <T> void print(T[] a) {
        ArrayUtil.print(a, ", ", "", "");
    }

    @Test
    public void testIntersect() {
        List<Integer> ls1 = Arrays.asList(4, 6, 1, 2, 5);
        List<Integer> ls2 = Arrays.asList(3, 9, 3, 8, 7);
        List<Integer> list = CollectionUtil.intersect(ls1, ls2);
        System.out.println(list);
    }

    @Test
    public void testUnion() {
        List<Integer> ls1 = Arrays.asList(4, 6, 1, 2, 5);
        List<Integer> ls2 = Arrays.asList(2, 1, 3, 1, 7);
        List<Integer> list = CollectionUtil.union(ls1, ls2);
        System.out.println(list);
    }

    @Test
    public void testDiff() {
        List<Integer> ls1 = Arrays.asList(4, 6, 1, 2, 5);
        List<Integer> ls2 = Arrays.asList(2, 1, 3, 1, 7);
        List<Integer> list = CollectionUtil.diff(ls1, ls2);
        System.out.println(list);
    }

    @Test
    public void testCombinationPair() {
        List<List<Integer>> a = Arrays.asList(Collections.singletonList(1),
                Collections.singletonList(2), Collections.singletonList(3));
        List<List<Integer>> b = Arrays.asList(Arrays.asList(4, 5), Arrays.asList(6, 7),
                Arrays.asList(8, 9));
        CollectionUtil.combinationPair(a, System.out::println);
        System.out.println("=============================");
        CollectionUtil.combinationPair(a, b, System.out::println);
    }

}
