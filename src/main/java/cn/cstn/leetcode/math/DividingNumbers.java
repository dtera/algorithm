package cn.cstn.leetcode.math;

import java.util.ArrayList;
import java.util.List;

/**
 * 728               Self Dividing Numbers
 * description :     A self-dividing number is a number that is divisible by every digit it contains.
 * For example, 128 is a self-dividing number because 128 % 1 == 0, 128 % 2 == 0, and 128 % 8 == 0.
 * Also, a self-dividing number is not allowed to contain the digit zero.
 * Given a lower and upper number bound, output a list of every possible self dividing number,
 * including the bounds if possible.
 * example 1 :
 * Input:
 * left = 1, right = 22
 * Output: [1, 2, 3, 4, 5, 6, 7, 8, 9, 11, 12, 15, 22]
 * note :
 * The boundaries of each input argument are 1 <= left <= right <= 10000.
 *
 * @author :        zhaohq
 * date :            2018-07-27 11:38
 */
public class DividingNumbers {
    public static void main(String[] args) {
        int left = 1, right = 22;
        System.out.println(selfDividingNumbers(left, right));
        System.out.println(_selfDividingNumbers(left, right));
    }

    private static List<Integer> _selfDividingNumbers(int left, int right) {
        List<Integer> res = new ArrayList<>();
        for (int i = left; i <= right; i++)
            if (isSelfDivided(i)) res.add(i);

        return res;
    }

    private static boolean isSelfDivided(int i) {
        int self = i, r;
        while (i != 0) {
            r = i % 10;
            if (r == 0 || self % r != 0) return false;
            i /= 10;
        }
        return true;
    }

    private static List<Integer> selfDividingNumbers(int left, int right) {
        List<Integer> res = new ArrayList<>();
        for (int i = left; i <= right; i++) {
            boolean isSelfDivided = true;
            for (char c : (i + "").toCharArray()) {
                if (c == '0' || i % (c - '0') != 0) {
                    isSelfDivided = false;
                    break;
                }
            }
            if (isSelfDivided) res.add(i);
        }
        return res;
    }
}
