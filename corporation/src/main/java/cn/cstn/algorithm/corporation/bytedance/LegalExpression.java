package cn.cstn.algorithm.corporation.bytedance;

import java.util.Scanner;

/**
 * 头条8.25             2. 合法的表达式
 * description :        我们定义合法的标识符为：数字0-9组成的字符串。（可以包含多个前导0）
 *                      定义合法的表达式为：
 *                          1.若X为合法的标识符，则X为合法的表达式
 *                          2.若X为合法的表达式，则（X）为合法的表达式
 *                          3.若X和Y均为合法的表达式，则X+Y，X-Y均为合法的表达式
 *                      如，以下均合法的表达式：1,100,1+2，（10），1-（3-2）
 *                      以下为不合法的表达式：（，-1,1+-2
 *                      给定长度n，求长为n的合法表达式的数目。长为n的合法表达式可能有非常多，你只需要输出
 *                      结果对1000000007取模的余数即可。
 *                      输入描述：一个整数n
 *                      输出描述：长为n的合法表达式的数目对1000000007取模的余数
 *                      示例：
 *                          输入  1
 *                          输出  10
 *                      备注：
 *                          对于40%的数据，n<=10
 *                          对于100%的数据，0<=n<=1000
 * @author :            zhaohq
 * date :               2018/9/3 0003 19:18
 */
public class LegalExpression {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();

        System.out.println(numOfLegalExpression(n));
    }

    private static int numOfLegalExpression(int n) {
        int mod=1000000007, m = n;
        //dp denotes the legal expression ending with digit,
        //whereas dp_ denotes the legal expression ending with parenthesis
        if (n < 4)  m = 3;
        int[] dp = new int[m + 1], dp_ = new int[m + 1];
        dp[0] = 0;
        dp[1] = 10;
        dp[2] = 100;
        dp[3] = 1000 + 200;
        dp_[0] = 0;
        dp_[1] = 0;
        dp_[2] = 0;
        dp_[3] = 10;
        for (int i = 4; i <= n; i++) {
            dp[i] = (int)Math.pow(10, i) % mod;
            dp_[i] = (dp[i - 2] + dp_[i - 2]) % mod;
            for (int j = i - 1; j > 1; j--) {
                dp[i] += 2 * dp[i - j] * (dp[j - 1] + dp_[j - 1]) % mod;
                dp_[i] += 2 * dp_[i - j] * (dp[j - 1] + dp_[j - 1]) % mod;
            }
        }

        return dp[n] + dp_[n];
    }

}
