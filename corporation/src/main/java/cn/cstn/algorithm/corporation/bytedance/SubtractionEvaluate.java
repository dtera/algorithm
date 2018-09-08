package cn.cstn.algorithm.corporation.bytedance;

import cn.cstn.algorithm.commons.KV;
import cn.cstn.algorithm.commons.Tuple;

import java.util.*;

/**
 * 8.25                 5.减法求值
 * description :        已知一些形如“y = 4 - x”的约束关系，查询形如“y - x”的值。
 *                      输入描述：
 *                          第一行为两个整数n，m，表示有n个已知的约束条件，有m个查询。
 *                          接下来n行，形如“y = k - x”表示约束条件y = k - x。其中等号和减号前后一定有空格。y与x是
 *                          变量名，有英文小写字母组成，长度不超过4。k是一个偶数（注意可能为负，负数的负号没有空格）
 *                          接下来m行，形如“y - x”，表示查询y - x的值。其中减号前后一定有空格。y与x是变量名，规则
 *                          同上。输入数据保证不会产生冲突，不会无解。
 *                      输出描述：
 *                          对于每个查询，输出一个整数，表示y - x的值。
 *                          若输入数据不足以推导出结果，则输出“cannot_answer”。
 *                      示例1：
 *                          输入
 *                          3 2
 *                          a = 0 - b
 *                          b = 2 - c
 *                          c = 4 - d
 *                          b - d
 *                          b - c
 *                          输出
 *                          -2
 *                          cannot_answer
 *                      备注：
 *                          对于30%的数据，0<=n<=1000，0<=m<=1000，变量名长度不超过2，对于所有的查询“y - x”，一定
 *                          能通过某条路径找到y与x的关系，如y=ka-a，a=kb-b，b=kc-c，c=kd-x
 *                          对于100%的数据，0<=n<=100000，0<=n<=100000，-200000<=k<=200000，变量名由小写英文字母组
 *                          成，且长度不超过4
 * @author :            zhaohq
 * date :               2018/9/6 0006 15:48
 */
@SuppressWarnings("unchecked")
public class SubtractionEvaluate {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String line = sc.nextLine();
        String[] ls = line.split(" ");
        int n = Integer.parseInt(ls[0]);
        int m = Integer.parseInt(ls[1]);
        Map<String, List<KV<Integer, String>>> cs = new HashMap<>();
        Tuple<String>[] qs = new Tuple[m];

        for (int i = 0; i < n; i++) {
            line = sc.nextLine();
            ls = line.split(" ");
            cs.computeIfAbsent(ls[0], k -> new ArrayList<>())
                    .add(new KV<>(Integer.parseInt(ls[2]), ls[4]));
            cs.computeIfAbsent(ls[4], k -> new ArrayList<>())
                    .add(new KV<>(Integer.parseInt(ls[2]), ls[0]));
        }
        for (int i = 0; i < m; i++) {
            line = sc.nextLine();
            ls = line.split(" ");
            qs[i] = new Tuple<>(ls[0], ls[2]);
        }

        subtractionEvaluate(cs, qs);
    }

    private static void subtractionEvaluate(Map<String, List<KV<Integer, String>>> cs, Tuple<String>[] qs) {
        for (Tuple<String> q : qs) {
            boolean hasAnswer = false;
            outer:
            for (KV<Integer, String> kv1 : cs.get(q._1())) {
                for (KV<Integer, String> kv2 : cs.get(q._2())) {
                    if (kv1._2().equals(kv2._2())) {
                        System.out.println(kv1._1() - kv2._1());
                        hasAnswer = true;
                        break outer;
                    }
                }
            }
            if (!hasAnswer) System.out.println("cannot_answer");
        }
    }
}
