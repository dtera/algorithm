package cn.cstn.algorithm.commons.test;

import cn.cstn.algorithm.commons.KV;
import org.junit.Test;

public class AppTest {
    @Test
    public void testKV() {
        KV<Integer, Integer> kv = new KV<>(1, 2);
        System.out.println(kv);
    }
}
