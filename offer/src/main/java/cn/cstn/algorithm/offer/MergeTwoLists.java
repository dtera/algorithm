package cn.cstn.algorithm.offer;

import cn.cstn.algorithm.commons.list.ListNode;

/**
 * 输入两个递增排序的链表，合并这两个链表并使新链表中的节点仍然是递增排序的。
 * <p>
 * <p>
 * 示例1：
 * <p>
 * 输入：1->2->4, 1->3->4
 * <p>
 * 输出：1->1->2->3->4->4
 * <p>
 * <p>
 * 限制：
 * <p>
 * 0 <= 链表长度 <= 1000
 * <p>
 *
 * @author zhaohuiqiang
 * @date 2020/6/26 9:31
 */
public class MergeTwoLists {

    public static void main(String[] args) {
        ListNode l1 = new ListNode(1);
        l1.next = new ListNode(2);
        l1.next.next = new ListNode(4);
        ListNode l2 = new ListNode(1);
        l2.next = new ListNode(3);
        l2.next.next = new ListNode(4);
        l2.next.next.next = new ListNode(5);
        l2.next.next.next.next = new ListNode(6);
        l1.println();
        l2.println();
        ListNode node = mergeTwoLists(l1, l2);
        node.println();
    }

    public static ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        /*if (l1 == null) return l2;
        if (l2 == null) return l1;
        ListNode head, cur;
        if (l1.val <= l2.val) {
            head = l1;
            l1 = l1.next;
        } else {
            head = l2;
            l2 = l2.next;
        }
        cur = head;*/
        ListNode dum = new ListNode(0), cur = dum;

        while (l1 != null && l2 != null) {
            if (l1.val <= l2.val) {
                cur.next = l1;
                l1 = l1.next;
            } else {
                cur.next = l2;
                l2 = l2.next;
            }
            cur = cur.next;
        }
        cur.next = l1 == null ? l2 : l1;

        return dum.next;
        //return head;
    }

}
