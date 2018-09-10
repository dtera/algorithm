package cn.cstn.algorithm.commons.test;

import cn.cstn.algorithm.commons.KV;
import cn.cstn.algorithm.commons.Tuple;
import org.junit.Test;

public class AppTest {

    @Test
    public void testKV() {
        KV<Integer, Integer> kv = new KV<>(1, 2);
        System.out.println(kv);
    }

    @Test
    public void testTuple() {
        Tuple<Integer> t = new Tuple<>(1, 2, 3, 4);
        System.out.println(t);
        System.out.println(t.g_(2));
        t.s_(2, 9);
        System.out.println(t.g_(2));
    }

}
