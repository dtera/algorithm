package cn.cstn.algorithm.leetcode.array;

import cn.cstn.algorithm.commons.util.CollectionUtil;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;

/**
 * description :        class description
 * @author :            zhaohq
 * date :               2018/8/28 0028 21:44
 */
public class MergeIntervals {
    public static void main(String[] args) {
        List<Pair<Integer, Integer>> ts = Arrays.asList(Pair.of(1,3),Pair.of(2,6),
                Pair.of(8,10),Pair.of(15,18));
        List<Pair<Integer, Integer>> lts = CollectionUtil.merge(ts);
        CollectionUtil.println(lts);
        lts = CollectionUtil._merge(ts);
        CollectionUtil.println(lts);
    }

}
