package cn.cstn.algorithm.offer;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 在字符串 s 中找出第一个只出现一次的字符。如果没有，返回一个单空格。 s 只包含小写字母。
 * <p>
 * <p>
 * 示例:
 * <p>
 * s = "abaccdeff"
 * <p>
 * 返回 "b"
 * <p>
 * s = ""
 * <p>
 * 返回 " "
 * <p>  
 * <p>
 * 限制：
 * <p>
 * 0 <= s 的长度 <= 50000
 * <p>
 *
 * @author zhaohuiqiang
 * @date 2020/7/21 15:31
 */
public class FirstUniqChar {

    public static void main(String[] args) {
        String s = "abaccdeff";
        System.out.println(firstUniqChar(s));
    }

    public static char firstUniqChar(String s) {
        Map<Character, Boolean> map = new LinkedHashMap<>();
        for (char c : s.toCharArray()) {
            map.put(c, !map.containsKey(c));
        }
        for (Map.Entry<Character, Boolean> entry : map.entrySet()) {
            if (entry.getValue()) return entry.getKey();
        }
        return ' ';
    }

}
