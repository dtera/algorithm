package cn.cstn.algorithm.corporation.huawei;

import cn.cstn.algorithm.commons.util.StringUtil;

import java.math.BigInteger;

/**
 * Factorial
 *
 * @author zhaohuiqiang
 * @date 2020/7/2 13:10
 */
public class Factorial {

    public static void main(String[] args) {
        System.out.println(StringUtil.factorial(100));
        System.out.println(factorial_(100));
    }

    public static BigInteger factorial_(int n) {
        BigInteger result = BigInteger.valueOf(1);
        for (int i = 2; i <= n; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        return result;
    }

}
