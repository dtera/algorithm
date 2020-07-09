package cn.cstn.algorithm.offer;

import cn.cstn.algorithm.commons.util.StringUtil;

/**
 * 输入一个字符串，打印出该字符串中字符的所有排列。
 * <p>
 * <p>
 * 你可以以任意顺序返回这个字符串数组，但里面不能有重复元素。
 * <p>
 * <p>
 * 示例:
 * <p>
 * 输入：s = "abc"
 * <p>
 * 输出：["abc","acb","bac","bca","cab","cba"]
 * <p>  
 * <p>
 * 限制：
 * <p>
 * 1 <= s 的长度 <= 8
 * <p>
 *
 * @author zhaohuiqiang
 * @date 2020/7/9 9:55
 */
public class StrPermutation {

    public static void main(String[] args) {
        String s = "aab";
        StringUtil.permutation(s, System.out::println);
    }

}
