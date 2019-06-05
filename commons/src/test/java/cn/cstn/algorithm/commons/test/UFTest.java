package cn.cstn.algorithm.commons.test;

import cn.cstn.algorithm.commons.UF;
import cn.cstn.algorithm.commons.graph.UF2D;
import cn.cstn.algorithm.commons.util.ArrayUtil;
import cn.cstn.algorithm.commons.util.BeanUtil;
import org.junit.Test;

import java.util.Arrays;

@SuppressWarnings("unused")
public class UFTest {

    @Test
    public void testUF() {
        int[][] c = {
                {1, 1, 0, 0, 1, 1, 0},
                {0, 1, 0, 0, 0, 1, 1},
                {1, 1, 1, 0, 0, 0, 0},
                {0, 0, 0, 0, 1, 0, 0},
                {0, 1, 0, 0, 0, 0, 1},
        };
        int m = c.length, n = c[0].length, r = 2;
        int[][] a = new int[m * r][n];
        for (int k = 0; k < r; k++)
            for (int i = 0; i < m; i++)
                System.arraycopy(c[i], 0, a[m * k + i], 0, n);

        long s = System.currentTimeMillis();
        UF2D uf2d = new UF2D(a);
        uf2d.buildConnectedComponent();
        long e = System.currentTimeMillis();

        int[][] connectedGraph = uf2d.getConnectedGraph();
        Arrays.stream(connectedGraph).forEach(ArrayUtil::println);

        int[] nums = uf2d.getNums();
        System.out.print("ComponentNums: ");
        ArrayUtil.println(nums);
        System.out.print("ComponentNums indexOfMinMax: ");
        ArrayUtil.println(ArrayUtil.indexOfMinMax(nums));
        System.out.println("kthMin for nums: " + ArrayUtil.kthMin(nums, nums.length));
        System.out.println("uf.find(2, 1): " + uf2d.find(2, 1));
        System.out.println("UF2D.isConnected(0, 0, 2, 1): " + uf2d.isConnected(0, 0, 2, 1));
        System.out.println("num of connected component:" + uf2d.getNumOfComponent() + "\t\tUF2D.connectedComponent costs " + (e - s) + "ms");

        System.out.println("****************************************************************************");
        s = System.currentTimeMillis();
        UF uf = new UF2D(a);
        uf.buildConnectedComponent(this::connectedComponent);
        e = System.currentTimeMillis();

        nums = uf.getNums();
        System.out.print("ComponentNums: ");
        ArrayUtil.println(nums);
        System.out.println("uf.find(0): " + uf.find(0));
        System.out.println("uf.isConnected(0, 1): " + uf.isConnected(0, 1));
        System.out.println("num of connected component:" + uf.getNumOfComponent() + "\t\tconnectedComponent costs " + (e - s) + "ms");
    }

    private void connectedComponent(UF uf) {
        UF2D uf2d = (UF2D) uf;
        int[][] a = uf2d.getA();
        int m = a.length, n = a[0].length;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (a[i][j] == 0) {
                    uf2d.decNumOfComponent();
                    continue;
                }
                if (i > 0 && j > 0 && a[i - 1][j - 1] != 0) uf2d.union(i, j, i - 1, j - 1);
                if (i > 0 && a[i - 1][j] != 0) uf2d.union(i, j, i - 1, j);
                if (i > 0 && j < n - 1 && a[i - 1][j + 1] != 0) uf2d.union(i, j, i - 1, j + 1);
                if (j > 0 && a[i][j - 1] != 0) uf2d.union(i, j, i, j - 1);
                if (j < n - 1 && a[i][j + 1] != 0) uf2d.union(i, j, i, j + 1);
                if (i < m - 1 && j > 0 && a[i + 1][j - 1] != 0) uf2d.union(i, j, i + 1, j - 1);
                if (i < m - 1 && a[i + 1][j] != 0) uf2d.union(i, j, i + 1, j);
                if (i < m - 1 && j < n - 1 && a[i + 1][j + 1] != 0) uf2d.union(i, j, i + 1, j + 1);
            }
        }

    }

    private UF _connectedComponent(int[][] a) {
        int m = a.length, n = a[0].length, count = 0;
        UF uf = new UF(m * n);

        if (a[0][0] != 0) {
            if (n > 1 && a[0][1] != 0) uf.union(0, 1);
            if (m > 1 && a[1][0] != 0) uf.union(0, n);
            if (m > 1 && n > 1 && a[1][1] != 0) uf.union(0, n + 1);
        } else count++;
        if (n > 1)
            if (a[0][n - 1] != 0) {
                if (a[0][n - 2] != 0) uf.union(n - 1, n - 2);
                if (m > 1 && a[1][n - 2] != 0) uf.union(n - 1, 2 * n - 2);
                if (m > 1 && a[1][n - 1] != 0) uf.union(n - 1, 2 * n - 1);
            } else count++;
        if (m > 1)
            if (a[m - 1][0] != 0) {
                if (a[m - 2][0] != 0) uf.union((m - 1) * n, (m - 2) * n);
                if (n > 1 && a[m - 2][1] != 0) uf.union((m - 1) * n, (m - 2) * n + 1);
                if (n > 1 && a[m - 1][1] != 0) uf.union((m - 1) * n, (m - 1) * n + 1);
            } else count++;
        if (m > 1 && n > 1)
            if (a[m - 1][n - 1] != 0) {
                if (a[m - 2][n - 2] != 0) uf.union(m * n - 1, (m - 1) * n - 2);
                if (a[m - 2][n - 1] != 0) uf.union(m * n - 1, (m - 1) * n - 1);
                if (a[m - 1][n - 2] != 0) uf.union(m * n - 1, m * n - 2);
            } else count++;

        for (int i = 1; i < m - 1; i++) {
            if (a[i][0] != 0) {
                if (a[i - 1][0] != 0) uf.union(i * n, (i - 1) * n);
                if (n > 1 && a[i - 1][1] != 0) uf.union(i * n, (i - 1) * n + 1);
                if (n > 1 && a[i][1] != 0) uf.union(i * n, i * n + 1);
                if (a[i + 1][0] != 0) uf.union(i * n, (i + 1) * n);
                if (n > 1 && a[i + 1][1] != 0) uf.union(i * n, (i + 1) * n + 1);
            } else count++;
            if (n > 1)
                if (a[i][n - 1] != 0) {
                    if (a[i - 1][n - 2] != 0) uf.union((i + 1) * n - 1, i * n - 2);
                    if (a[i - 1][n - 1] != 0) uf.union((i + 1) * n - 1, i * n - 1);
                    if (a[i][n - 2] != 0) uf.union((i + 1) * n - 1, (i + 1) * n - 2);
                    if (a[i + 1][n - 2] != 0) uf.union((i + 1) * n - 1, (i + 2) * n - 2);
                    if (a[i + 1][n - 1] != 0) uf.union((i + 1) * n - 1, (i + 2) * n - 1);
                } else count++;
        }
        for (int j = 1; j < n - 1; j++) {
            if (a[0][j] != 0) {
                if (a[0][j - 1] != 0) uf.union(j, j - 1);
                if (a[0][j + 1] != 0) uf.union(j, j + 1);
                if (m > 1 && a[1][j - 1] != 0) uf.union(j, n + j - 1);
                if (m > 1 && a[1][j] != 0) uf.union(j, n + j);
                if (m > 1 && a[1][j + 1] != 0) uf.union(j, n + j + 1);
            } else count++;
            if (m > 1)
                if (a[m - 1][j] != 0) {
                    if (a[m - 2][j - 1] != 0) uf.union((m - 1) * n + j, (m - 2) * n + j - 1);
                    if (a[m - 2][j] != 0) uf.union((m - 1) * n + j, (m - 2) * n + j);
                    if (a[m - 2][j + 1] != 0) uf.union((m - 1) * n + j, (m - 2) * n + j + 1);
                    if (a[m - 1][j - 1] != 0) uf.union((m - 1) * n + j, (m - 1) * n + j - 1);
                    if (a[m - 1][j + 1] != 0) uf.union((m - 1) * n + j, (m - 1) * n + j + 1);
                } else count++;
        }


        for (int i = 1; i < m - 1; i++) {
            for (int j = 1; j < n - 1; j++) {
                if (a[i][j] == 0) {
                    count++;
                    continue;
                }
                if (a[i - 1][j - 1] != 0) uf.union((i - 1) * n + j - 1, i * n + j);
                if (a[i - 1][j] != 0) uf.union((i - 1) * n + j, i * n + j);
                if (a[i - 1][j + 1] != 0) uf.union((i - 1) * n + j + 1, i * n + j);
                if (a[i][j - 1] != 0) uf.union(i * n + j - 1, i * n + j);
                if (a[i][j + 1] != 0) uf.union(i * n + j + 1, i * n + j);
                if (a[i + 1][j - 1] != 0) uf.union((i + 1) * n + j - 1, i * n + j);
                if (a[i + 1][j] != 0) uf.union((i + 1) * n + j, i * n + j);
                if (a[i + 1][j + 1] != 0) uf.union((i + 1) * n + j + 1, i * n + j);
            }
        }

        BeanUtil.setFieldValue(uf, "numOfComponent", uf.getNumOfComponent() - count);

        return uf;
    }

}
