package cn.cstn.algorithm.commons.test;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.Test;

public class AppTest {

    @Test
    public void testKV() {
        Pair<Integer, Integer> kv = Pair.of(1, 2);
        System.out.println(kv);
    }

    @Test
    public void testTuple() {
        Triple<Integer, Integer, Integer> t = Triple.of(1, 2, 3);
        System.out.println(t);
        System.out.println(t.getLeft());
        System.out.println(t.getRight());
    }

}
