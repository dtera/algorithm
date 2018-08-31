package cn.cstn.algorithm.leetcode.string;

/**
 * 709               To Lower Case
 * description :     Implement function ToLowerCase() that has a string parameter str,
 *                   and returns the same string in lowercase.
 * @author :         zhaohq
 * date :            2018-07-26 18:17
 */
public class LowerCase {
    public static void main(String[] args) {
        String str = "Hello&World";
        String s = toLowerCase(str);
        System.out.println(s);
    }

    private static String toLowerCase(String str) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if(c - 'A' >= 0 && c - 'Z' <= 0)
                c = (char) (c + 32);
            sb.append(c);
        }

        return sb.toString();
    }
}
