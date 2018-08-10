package cn.cstn.algorithm.leetcode.hashtable;

import java.util.HashMap;

/**
 * 771               Jewels and Stones
 * description :     You're given strings J representing the types of stones that are jewels,
 *                   and S representing the stones you have.  Each character in S is a type of stone you have.
 *                   You want to know how many of the stones you have are also jewels.
 *                   The letters in J are guaranteed distinct, and all characters in J and S are letters.
 *                   Letters are case sensitive, so "a" is considered a different type of stone from "A".
 * @author :        zhaohq
 * date :            2018-07-26 18:40
 */
public class JewelsInStones {

    public static void main(String[] args) {
        String J = "aA", S = "aAAb";
        int num = numJewelsInStones(J, S);
        int num1 = _numJewelsInStones(J, S);
        System.out.println(num);
        System.out.println(num1);
    }

    private static int _numJewelsInStones(String J, String S) {
        int num = 0;
        HashMap<Character, Character> map = new HashMap<>();
        for(int i = 0; i < J.length(); i++)
            map.put(J.charAt(i), J.charAt(i));

        for(int i = 0; i < S.length(); i++)
            if (map.containsKey(S.charAt(i))) num++;
        return num;
    }

    private static int numJewelsInStones(String J, String S) {
        int num = 0;
        for(int i = 0; i < J.length(); i++) {
            char c = J.charAt(i);
            for(int j = 0; j < S.length(); j++)
                if (c == S.charAt(j)) num++;
        }
        return num;
    }
}
