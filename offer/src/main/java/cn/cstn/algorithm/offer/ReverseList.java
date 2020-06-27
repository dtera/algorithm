package cn.cstn.algorithm.offer;

import cn.cstn.algorithm.commons.list.ListNode;

/**
 * 定义一个函数，输入一个链表的头节点，反转该链表并输出反转后链表的头节点。
 * <p>
 * <p>
 * 示例:
 * <p>
 * 输入: 1->2->3->4->5->NULL
 * <p>
 * 输出: 5->4->3->2->1->NULL
 *  
 * <p>
 * 限制：
 * <p>
 * 0 <= 节点个数 <= 5000
 * <p>
 *
 * @author zhaohuiqiang
 * @date 2020/6/26 9:02
 */
public class ReverseList {

    public static void main(String[] args) {
        ListNode head = new ListNode(1);
        head.next = new ListNode(2);
        head.next.next = new ListNode(3);
        head.next.next.next = new ListNode(4);
        head.next.next.next.next = new ListNode(5);
        head.println();
        ListNode node = reverseList(head);
        node.println();
    }

    public static ListNode reverseList(ListNode head) {
        if (head == null) return null;

        ListNode cur = head;
        while (head.next != null) {
            ListNode t = head.next.next;
            head.next.next = cur;
            cur = head.next;
            head.next = t;
        }

        return cur;
    }

}
