package cn.cstn.algorithm.corporation.other;

import cn.cstn.algorithm.commons.util.StringUtil;

/**
 * LongestCommonSubSeq
 *
 * @author zhaohuiqiang
 * @date 2020/7/15 10:32
 */
public class LongestCommonSubSeq {

    public static void main(String[] args) {
        String a = "leetcode";
        String b = "coder";
        System.out.println(StringUtil.lcs(a, b));
    }

}
