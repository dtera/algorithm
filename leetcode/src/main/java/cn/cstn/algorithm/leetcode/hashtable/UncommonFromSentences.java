package cn.cstn.algorithm.leetcode.hashtable;

import cn.cstn.algorithm.leetcode.util.ArrayHelper;

import java.util.*;

/**
 * 888                  Uncommon Words from Two Sentences
 * description :        We are given two sentences A and B.  (A sentence is a string of space separated words.
 *                      Each word consists only of lowercase letters.)
 *                      A word is uncommon if it appears exactly once in one of the sentences,
 *                      and does not appear in the other sentence.
 *                      Return a list of all uncommon words.
 *                      You may return the list in any order.
 *                      Example 1:
 *                              Input: A = "this apple is sweet", B = "this apple is sour"
 *                              Output: ["sweet","sour"]
 *                      Example 2:
 *                              Input: A = "apple apple", B = "banana"
 *                              Output: ["banana"]
 *                      Note:
 *                              0 <= A.length <= 200
 *                              0 <= B.length <= 200
 *                              A and B both contain only spaces and lowercase letters.
 * @author :           zhaohq
 * date :               2018/8/12 0012 15:19
 */
public class UncommonFromSentences {
    public static void main(String[] args) {
        String A = "this apple is sweet", B = "this apple is sour";
        String[] ufs = uncommonFromSentences(A, B);
        ArrayHelper.println(ufs);
    }

    private static String[] uncommonFromSentences(String A, String B) {
        List<String> list = new ArrayList<>();
        String[] as = A.split(" ");
        String[] bs = B.split(" ");
        HashMap<String, Integer> m = new HashMap<>();
        saveToMap(as, m);
        saveToMap(bs, m);
        for (Map.Entry<String, Integer> entry: m.entrySet())
            if (entry.getValue() < 2) list.add(entry.getKey());

        return list.toArray(new String[0]);
    }

    private static void saveToMap(String[] ss, HashMap<String, Integer> m) {
        for (String s: ss) {
            Integer count = m.get(s);
            if (count == null) count = 0;
            m.put(s, ++count);
        }
    }
}
