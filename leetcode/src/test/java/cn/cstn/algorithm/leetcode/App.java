package cn.cstn.algorithm.leetcode;

import cn.cstn.algorithm.commons.util.ArrayHelper;
import cn.cstn.algorithm.commons.util.UF;
import org.junit.Test;

public class App {

    @Test
    public void testUF() {
        int[][] a = {
                {1, 1, 0, 0, 1, 1, 0},
                {0, 1, 0, 0, 0, 1, 1},
                {1, 1, 1, 0, 0, 0, 0},
                {0, 0, 0, 0, 1, 0, 0},
                {1, 1, 0, 0, 0, 0, 1},
                {1, 0, 0, 1, 0, 1, 1},
                {1, 1, 0, 1, 0, 0, 1}
        };
        int m = a.length, n = a[0].length, count = 0;
        int[][] b = new int[m][n];
        UF uf = new UF(m * n);
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (a[i][j] == 0) {
                    count++;
                    continue;
                }
                if (i > 0 && j > 0 && a[i - 1][j - 1] == 1) uf.union((i - 1) * n + j - 1, i * n + j);
                if (i > 0 && a[i - 1][j] == 1) uf.union((i - 1) * n + j, i * n + j);
                if (i > 0 && j < n - 1 && a[i - 1][j + 1] == 1) uf.union((i - 1) * n + j + 1, i * n + j);
                if (j > 0 && a[i][j - 1] == 1) uf.union(i * n + j - 1, i * n + j);
                if (j < n - 1 && a[i][j + 1] == 1) uf.union(i * n + j + 1, i * n + j);
                if (i < m - 1 && j > 0 && a[i + 1][j - 1] == 1) uf.union((i + 1) * n + j - 1, i * n + j);
                if (i < m - 1 && a[i + 1][j] == 1) uf.union((i + 1) * n + j, i * n + j);
                if (i < m - 1 && j < n - 1 && a[i + 1][j + 1] == 1) uf.union((i + 1) * n + j + 1, i * n + j);
            }
        }

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                b[i][j] = uf.find(i * n + j);
                if (a[i][j] == 0) b[i][j] = 0;
            }
        }

        for (int[] i : a) ArrayHelper.println(i);
        System.out.println("==============================");
        for (int i = 0; i < m; i++)
            ArrayHelper.println(b[i]);

        System.out.println(uf.getCount() - count);
        System.out.println(uf.isConnected(0, 16));
    }
}
