package cn.cstn.algorithm.corporation.huawei;

import java.util.Scanner;

/**
 * 华为2018.3.21        1.最长数字字符串
 * description :        给定一个字符串，输出字符串中最长的数字串，并把这个数字串的长度输出。
 *                      请在字符串中找出连续最长的数字串，并把这个串的长度返回；如果存在长度相同的连续数字串，
 *                      返回最后一个连续数字串；
 *                      注意：数字串只需要是数字组成的就可以，并不要求顺序，比如数字串“1234”的长度就小于数字串
 *                      “1359055”，如果没有数字，则返回空字符串（“”）而不是NULL！
 *                      输入描述:   一个字符串
 *                      输出描述:   输出最长的数字串,输出最长数字串个数,中间以逗号(,)隔开；
 *                      示例1
 *                          输入  abcd12345ed125ss123058789
 *                          输出  123058789,9
 * @author :            zhaohq
 * date :               2018-09-12 17:11
 */
public class LongestNumericString {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String s = sc.next();
        int ml = 0;
        String ms = "";
        for (int i = 0; i < s.length(); i++) {
            StringBuilder sb = new StringBuilder();
            int cm = 0;
            while (i < s.length() && Character.isDigit(s.charAt(i))) {
                sb.append(s.charAt(i));
                cm++;
                i++;
            }
            if (cm >= ml) {
                ms = sb.toString();
                ml = cm;
            }
        }

        System.out.println(ms + "," + ml);
    }

}
