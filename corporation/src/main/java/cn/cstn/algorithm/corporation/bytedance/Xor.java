package cn.cstn.algorithm.corporation.bytedance;

import java.util.Scanner;

/**
 * description :
 *                      题目描述:
 *                              给定整数m以及n各数字A1,A2,..An，将数列A中所有元素两两异或，共能得到n(n-1)/2个结果，
 *                              请求出这些结果中大于m的有多少个。
 *                      输入描述:
 *                              第一行包含两个整数n,m.
 *                              第二行给出n个整数A1，A2，...，An。
 *                      数据范围:
 *                              对于30%的数据，1 <= n, m <= 1000
 *                              对于100%的数据，1 <= n, m, Ai <= 10^5
 *                      输出描述:
 *                              输出仅包括一行，即所求的答案
 *                      示例1
 *                      输入    3 10
 *                              6 5 10
 *                      输出    2
 * @author :           zhaohq
 * date :               2018/8/10 0010 8:48
 */
public class Xor {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt(), m = sc.nextInt();
        int[] arr = new int[n];
        for (int i = 0; i < n; i++)
            arr[i] = sc.nextInt();

        System.out.println(xor(arr, m));
        System.out.println(_xor(arr, m));
    }

    private static long _xor(int[] arr, int m) {
        long count = 0;
        TrieNode trie = buildTrieTree(arr);
        System.out.println("root.count: " + trie.count);
        for (int a : arr)
            count += queryTrieTree(trie, a, m, 31);

        return count / 2;
    }

    private static long queryTrieTree(TrieNode trie, int a, int m, int k) {
        if (trie == null) return 0;

        TrieNode cur = trie;
        for (int i = k; i >= 0; i--) {
            int ad = (a >> i) & 1;
            int md = (m >> i) & 1;
            if (ad == 1 && md == 1) {
                if (cur.children[0] == null) return 0;
                cur = cur.children[0];
            } else if (ad == 1 && md == 0) {
                int p = cur.children[0] == null ? 0 : cur.children[0].count;
                return p + queryTrieTree(cur.children[1], a, m, i - 1);
            } else if (ad == 0 && md == 1) {
                if (cur.children[1] == null) return 0;
                cur = cur.children[1];
            } else if (ad == 0 && md == 0) {
                int q = cur.children[1] == null ? 0 : cur.children[1].count;
                return queryTrieTree(cur.children[0], a, m, i - 1) + q;
            }
        }
        return 0;
    }

    private static TrieNode buildTrieTree(int[] arr) {
        TrieNode root = new TrieNode();
        for (int a : arr) {
            TrieNode cur = root;
            for (int i = 31; i >= 0; i--) {
                int d = (a >> i) & 1;
                if (cur.children[d] == null) cur.children[d] = new TrieNode();
                cur.children[d].count++;
                cur = cur.children[d];
            }
        }

        TrieNode p = root.children[0], q = root.children[1];
        if (p != null && q != null)
            root.count = p.count + q.count;
        else if (p != null)
            root.count = p.count;
        else if (q != null)
            root.count = q.count;

        return root;
    }

    private static class TrieNode {
        TrieNode[] children = new TrieNode[2];
        int count = 0;
    }

    private static long xor(int[] arr, int m) {
        long count = 0;
        int n = arr.length;
        for (int i = 0; i < n - 1; i++)
            for (int j = i + 1; j < n; j++)
                if ((arr[i] ^ arr[j]) > m) count++;
        return count;
    }

}
