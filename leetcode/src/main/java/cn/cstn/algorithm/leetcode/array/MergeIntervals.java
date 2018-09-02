package cn.cstn.algorithm.leetcode.array;

import cn.cstn.algorithm.commons.Tuple;
import cn.cstn.algorithm.commons.util.CollectionUtil;

import java.util.Arrays;
import java.util.List;

/**
 * description :        class description
 * @author :            zhaohq
 * date :               2018/8/28 0028 21:44
 */
public class MergeIntervals {
    public static void main(String[] args) {
        List<Tuple<Integer>> ts = Arrays.asList(new Tuple<>(1,3),new Tuple<>(2,6),
                new Tuple<>(8,10),new Tuple<>(15,18));
        List<Tuple<Integer>> lts = CollectionUtil.merge(ts);
        CollectionUtil.println(lts);
        lts = CollectionUtil._merge(ts);
        CollectionUtil.println(lts);
    }

}
