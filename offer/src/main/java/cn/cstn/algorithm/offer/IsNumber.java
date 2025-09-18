package cn.cstn.algorithm.offer;

import cn.cstn.algorithm.commons.graph.DFA;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 请实现一个函数用来判断字符串是否表示数值（包括整数和小数）。例如，字符串"+100"、"5e2"、"-123"、"3.1416"、"0123"都表示数值，
 * 但"12e"、"1a3.14"、"1.2.3"、"+-5"、"-1E-16"及"12e+5.4"都不是。
 *
 * @author zhaohuiqiang
 * @date 2020/6/23 11:21
 */
@SuppressWarnings("unused")
public class IsNumber {

    public static void main(String[] args) {
        String s = "234.e-2";
        System.out.println(isNumber(s));
        System.out.println(isNumber_(s));
    }

    public static boolean isNumber(String s) {
        if (s == null) return false;
        int[][] transTable = {
                {1, 2, -1, 3, 0, -1},
                {-1, 2, -1, 3, -1, -1},
                {-1, 2, 6, 4, 9, -1},
                {-1, 5, -1, -1, -1, -1},
                {-1, 5, 6, -1, 9, -1},
                {-1, 5, 6, -1, 9, -1},
                {7, 8, -1, -1, -1, -1},
                {-1, 8, -1, -1, -1, -1},
                {-1, 8, -1, -1, 9, -1},
                {-1, -1, -1, -1, 9, -1},
        };

        List<Integer> legalStates = new ArrayList<>() {{
          add(2);
          add(4);
          add(5);
          add(8);
          add(9);
        }};

        /*Map<String, Integer> columns = new HashMap<String, Integer>() {{
            put("sign", 0);
            put("num", 1);
            put("exp", 2);
            put("dot", 3);
            put("blank", 4);
            put("other", 5);
        }};

        int state = 0;
        for (int i = 0; i < s.length(); i++) {
            int col = getColOf(s.charAt(i), columns);
            state = transTable[state][col];
            if (state == -1) return false;
        }

        return legalStates.contains(state);*/

        DFA dfa = DFA.of(transTable, legalStates);
        return dfa.accept(s);
    }

    private static int getColOf(char c, Map<String, Integer> columns) {
        if (c == '+' || c == '-') {
            return columns.get("sign");
        } else if (c >= '0' && c <= '9') {
            return columns.get("num");
        } else if (c == 'e' || c == 'E') {
            return columns.get("exp");
        } else if (c == '.') {
            return columns.get("dot");
        } else if (c == ' ') {
            return columns.get("blank");
        }

        return columns.get("other");
    }

    public static boolean isNumber_(String s) {
        if (s == null) return false;
        s = s.trim();
        boolean numSeen = false, dotSeen = false, eSeen = false;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= '0' && c <= '9') {
                numSeen = true;
            } else if (c == '.') {
                if (dotSeen || eSeen) {
                    return false;
                }
                dotSeen = true;
            } else if (c == 'e' || c == 'E') {
                if (eSeen || !numSeen) {
                    return false;
                }
                eSeen = true;
                numSeen = false;
            } else if (c == '+' || c == '-') {
                if (i != 0 && s.charAt(i - 1) != 'e' && s.charAt(i - 1) != 'E') {
                    return false;
                }
            } else {
                return false;
            }
        }

        return numSeen;
    }

}
