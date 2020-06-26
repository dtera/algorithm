package cn.cstn.algorithm.offer;

import lombok.RequiredArgsConstructor;

/**
 * ListNode
 *
 * @author zhaohuiqiang
 * @date 2020/6/26 8:38
 */
@RequiredArgsConstructor
public class ListNode {
    public final int val;
    public ListNode next;

    public void println() {
        StringBuilder sb = new StringBuilder("[");
        ListNode cur = this;
        sb.append(cur.val);
        cur = cur.next;
        while (cur != null) {
            sb.append(", ").append(cur.val);
            cur = cur.next;
        }
        sb.append("]");
        System.out.println(sb.toString());
    }

}