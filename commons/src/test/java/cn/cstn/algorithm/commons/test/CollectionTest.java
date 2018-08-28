package cn.cstn.algorithm.commons.test;

import cn.cstn.algorithm.commons.util.ArrayUtil;
import cn.cstn.algorithm.commons.util.CollectionUtil;
import org.junit.Test;

import java.util.Arrays;
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

}
