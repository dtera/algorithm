package cn.cstn.algorithm.commons.util;

/**
 * description :        Math class
 * @author :            zhaohq
 * date :               2018-09-09 22:53
 */
public class Math {

    public static int gcd(int m, int n) {
        //return m % n == 0 ? n : gcd(n, m % n);
        int r = m % n;
        while (r != 0) {
            m = n;
            n = r;
            r = m % n;
        }

        return n;
    }

}
