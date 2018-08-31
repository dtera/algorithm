package cn.cstn.algorithm.leetcode.string;

import java.util.HashSet;
import java.util.Set;

/**
 * 804               Unique Morse Code Words
 * description :     International Morse Code defines a standard encoding where each letter is mapped to a series of dots and dashes,
 *                   as follows: "a" maps to ".-", "b" maps to "-...", "c" maps to "-.-.", and so on.
 *                   For convenience, the full table for the 26 letters of the English alphabet is given below:
 *                   [".-","-...","-.-.","-..",".","..-.","--.","....","..",".---","-.-",".-..","--",
 *                   "-.","---",".--.","--.-",".-.","...","-","..-","...-",".--","-..-","-.--","--.."]
 *                   Now, given a list of words, each word can be written as a concatenation of the Morse code of each letter.
 *                   For example, "cab" can be written as "-.-.-....-", (which is the concatenation "-.-." + "-..." + ".-").
 *                   We'll call such a concatenation, the transformation of a word.
 *                   Return the number of different transformations among all words we have.
 *
 * example:
 *                   Input: words = ["gin", "zen", "gig", "msg"]
 *                   Output: 2
 *                   Explanation:
 *                   The transformation of each word is:
 *                   "gin" -> "--...-."
 *                   "zen" -> "--...-."
 *                   "gig" -> "--...--."
 *                   "msg" -> "--...--."
 *                   There are 2 different transformations, "--...-." and "--...--.".
 * note:
 *                   The length of words will be at most 100.
 *                   Each words[i] will have length in range [1, 12].
 *                   words[i] will only consist of lowercase letters.
 * @author :         zhaohq
 * date :            2018-07-26 18:16
 */
public class MorseRepresentations {
    public static void main(String[] args) {
        String[] words = {"gin", "zen", "gig", "msg"};
        System.out.println(uniqueMorseRepresentations(words));
    }

    private static int uniqueMorseRepresentations(String[] words) {
        Set<String> ws = new HashSet<>();
        String[] morseCodes = {".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..", ".---", "-.-", ".-..", "--", "-.", "---", ".--.", "--.-", ".-.", "...", "-", "..-", "...-", ".--", "-..-", "-.--", "--.."};

        for (String w : words) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < w.length(); i++) {
                sb.append(morseCodes[w.charAt(i) - 'a']);
            }
            ws.add(sb.toString());
        }

        return ws.size();
    }
}
