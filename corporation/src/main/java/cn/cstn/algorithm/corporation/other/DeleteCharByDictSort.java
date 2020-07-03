package cn.cstn.algorithm.corporation.other;

import cn.cstn.algorithm.commons.util.StringUtil;

/**
 * DeleteCharByDictSort
 *
 * @author zhaohuiqiang
 * @date 2020/7/3 11:28
 */
public class DeleteCharByDictSort {

    public static void main(String[] args) {
        String s = "fhdeerebga";
        String res = StringUtil.deleteCharByDictSort(s, 4);
        System.out.println(res);
    }

}
