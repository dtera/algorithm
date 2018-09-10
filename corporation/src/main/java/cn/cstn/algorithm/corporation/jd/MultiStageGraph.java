package cn.cstn.algorithm.corporation.jd;

import cn.cstn.algorithm.commons.util.ArrayUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**京东2018.9.9         1.完全多部图
 * description :        给定一张包含N个点、M条边的无向图，每条边连接两个不同的点，且任意两点间最多只有
 *                      一条边。对于这样简单的无向图，如果能将所有点划分成若干个集合，使得任意两个同一
 *                      集合内的点之间没有边相连，任意两个不同集合内的点之间有边相连，则称该图为完全多
 *                      部图。现在你需要判断给定的图是否为完全多部图。
 *                      输入：
 *                          第一行输入一个整数T表示数据组数，1<=T<=10。每组数据的格式为：
 *                          第一行包含两个整数N和M，1<=N<=1000,0<=M<=N(N-1)/2；
 *                          接下来M行，每行包含两个整数X和Y，表示第X个点和第Y个点之间有一条边，
 *                          1<=X，Y<=N。
 *                      输出：
 *                          魅族输出占一行，如果给定的图为完全多部图，输出Yes，否则输出No。
 * @author :            zhaohq
 * date :               2018-09-10 8:58
 */
@Slf4j @SuppressWarnings("unchecked")
public class MultiStageGraph {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int t = sc.nextInt();
        for (int i = 0; i < t; i++) {
            int n = sc.nextInt();
            int m = sc.nextInt();
            Set<Integer>[] g = new HashSet[n];
            for (int j = 0; j < m; j++) {
                int p = sc.nextInt() - 1;
                int q = sc.nextInt() - 1;
                if (g[p] == null) g[p] = new HashSet<>();
                g[p].add(q);
                if (g[q] == null) g[q] = new HashSet<>();
                g[q].add(p);
            }

            if (ArrayUtil.isMultiStageGraph(g)) System.out.println("Yes");
            else System.out.println("No");
        }
    }

}
