package cn.cstn.algorithm.offer;

import cn.cstn.algorithm.commons.list.ListNode;

/**
 * 输入一个链表，输出该链表中倒数第k个节点。为了符合大多数人的习惯，本题从1开始计数，即链表的尾节点是倒数第1个节点。
 * 例如，一个链表有6个节点，从头节点开始，它们的值依次是1、2、3、4、5、6。这个链表的倒数第3个节点是值为4的节点。
 * <p>
 *  
 * <p>
 * 示例：
 * <p>
 * 给定一个链表: 1->2->3->4->5, 和 k = 2.
 * <p>
 * 返回链表 4->5.
 * <p>
 *
 * @author zhaohuiqiang
 * @date 2020/6/26 8:25
 */
public class LinkedListKthFromEnd {

    public static void main(String[] args) {
        ListNode head = new ListNode(4);
        head.next = new ListNode(5);
        head.next.next = new ListNode(1);
        head.next.next.next = new ListNode(9);
        head.println();
        ListNode node = getKthFromEnd(head, 3);
        node.println();
    }


    public static ListNode getKthFromEnd(ListNode head, int k) {
        ListNode cur = head, pre = head;
        for (int i = 0; i < k; i++) {
            pre = pre.next;
        }
        while (pre != null) {
            pre = pre.next;
            cur = cur.next;
        }
        return cur;
    }

}
