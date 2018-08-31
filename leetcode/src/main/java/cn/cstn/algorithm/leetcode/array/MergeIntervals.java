package cn.cstn.algorithm.leetcode.array;

import cn.cstn.algorithm.commons.math.Point;
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
        List<Point> ps = Arrays.asList(new Point(1,3),new Point(2,6),
                new Point(8,10),new Point(15,18));
        List<Point> lps = CollectionUtil.merge(ps);
        CollectionUtil.println(lps);
        lps = CollectionUtil._merge(ps);
        CollectionUtil.println(lps);
    }

}
