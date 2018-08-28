package cn.cstn.algorithm.commons.test;

import cn.cstn.algorithm.commons.util.ArrayUtil;
import org.junit.Test;

public class ArrayTest {
    @Test
    public void testPrint() {
        int[] a = {4, 6, 1, 8, 5, 3, 9, 7, 2};
        Integer[] b = {4, 6, 1, 8, 5, 3, 9, 7, 2};
        ArrayUtil.println(a);
        ArrayUtil.print(a, ", ", "", "\n");
        ArrayUtil.println(b);
        ArrayUtil.print(b, ", ", "", "\n");
    }
}
