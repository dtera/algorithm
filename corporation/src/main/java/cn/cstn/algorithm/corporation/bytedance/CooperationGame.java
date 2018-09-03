package cn.cstn.algorithm.corporation.bytedance;

import cn.cstn.algorithm.commons.UF;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Scanner;

/**
 * 头条8.25             1. 字节跳动大闯关
 * description :        Byte dance Efficiency Engineering团队在8月20日搬入了学清嘉创大厦。
 *                      为庆祝团队的乔迁之喜，字节君决定邀请整个EE团队，举办一个大型团建游戏-字节跳动大闯关。
 *                      可是遇到了一个问题：
 *                      EE团队共有n个人，大家都比较害羞，不善于与陌生人交流。这n个人每个人都向字节君提供了
 *                      自己认识人的名字，不包括自己。如果A的名单里有B，或B的名单里有A，则代表A与B相互认识。
 *                      同时如果A认识B，B认识C，则代表A与C也会很快认识，毕竟通过B的介绍，两个人就可以很快相
 *                      互认识的了。
 *                      为了大闯关游戏可以更好地团队协作、气氛更活跃，并使得团队中的人可以尽快的相互了解、
 *                      认识和交流，字节君决定根据这个名单将团队分为m组，每组人数可以不同，但组内的任何一
 *                      个人都与组内的其他所有人直接或间接的认识和交流。如何确定一个方案，使得团队可以分成
 *                      m组，并且这个m尽可能地小呢？
 *                      输入描述：
 *                          第一行一个整数n，代表有n个人，从1开始编号。
 *                          接下来有n行，第x+1行代表编号为x的人认识的人的编号k（1<=k<=n），每个人的名单以0
 *                          代表结束。
 *                      输出描述：
 *                          一个整数m，代表可以分的最小的组的个数。
 *                      输入：
 *                          10
 *                          0
 *                          5 3 0
 *                          8 4 0
 *                          9 0
 *                          9 0
 *                          3 0
 *                          0
 *                          7 9 0
 *                          0
 *                          9 7 0
 *                      输出：2
 *                      说明：
 *                          1号同学孤独的自己一个组，因为他谁也不认识，也没有人认识他。
 *                          其他所有人均可以直接或间接的认识，分在同一个组。
 *                      备注：
 *                          对于50%的数据，n<=1000
 *                          对于100%的数据，1<=n<=100000, 名单总长度不超过100000
 * @author :            zhaohq
 * date :               2018/9/3 0003 14:00
 */
@Slf4j
public class CooperationGame {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        UF uf = new UF(n);
        for (int i = 0; i < n; i++)
            for (int k = sc.nextInt();k != 0; k = sc.nextInt()) uf.union(i, k - 1);

        System.out.println(uf.getNumOfComponent());
        solve();
    }

    private static void solve() {
        Scanner sc = new Scanner(System.in);
        int n = Integer.parseInt(sc.nextLine());
        UF uf = new UF(n);
        for (int i = 0; i < n; i++) {
            String line = sc.nextLine();
            String[] is = line.split("\\s");
            log.debug(Arrays.toString(is));
            for (String ai: is)
                if (!"0".equals(ai) && !"".equals(ai))
                    uf.union(i, Integer.parseInt(ai) - 1);
        }

        System.out.println(uf.getNumOfComponent());
    }

}
