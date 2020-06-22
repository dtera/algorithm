package cn.cstn.algorithm.offer;

/**
 * 给定单向链表的头指针和一个要删除的节点的值，定义一个函数删除该节点。
 * <p>
 * 返回删除后的链表的头节点。
 * <p>
 * 注意：此题对比原题有改动
 * <p>
 * <p>
 * 示例 1:
 * <p>
 * 输入: head = [4,5,1,9], val = 5
 * <p>
 * 输出: [4,1,9]
 * <p>
 * 解释: 给定你链表中值为 5 的第二个节点，那么在调用了你的函数之后，该链表应变为 4 -> 1 -> 9.
 * <p>
 * <p>
 * 示例 2:
 * <p>
 * 输入: head = [4,5,1,9], val = 1
 * <p>
 * 输出: [4,5,9]
 * <p>
 * 解释: 给定你链表中值为 1 的第三个节点，那么在调用了你的函数之后，该链表应变为 4 -> 5 -> 9.
 * <p>
 * <p>
 * 说明：
 * <p>
 * 题目保证链表中节点的值互不相同
 * <p>
 * 若使用 C 或 C++ 语言，你不需要 free 或 delete 被删除的节点
 *
 * @author zhaohuiqiang
 * @date 2020/6/22 10:36
 */
public class DeleteNode {

    public static void main(String[] args) {
        ListNode head = new ListNode(4);
        head.next = new ListNode(5);
        head.next.next = new ListNode(1);
        head.next.next.next = new ListNode(9);
        printListNode(head);
        ListNode node = deleteNode(head, 1);
        printListNode(node);
    }

    public static ListNode deleteNode(ListNode head, int val) {
        if (head == null) return null;
        if (head.val == val) return head.next;
        ListNode cur = head;
        while (cur.next != null && cur.next.val != val) {
            cur = cur.next;
        }
        if (cur.next != null) {
            cur.next = cur.next.next;
        }
        return head;
    }

    public static void printListNode(ListNode head) {
        StringBuilder sb = new StringBuilder("[");
        ListNode cur = head;
        if (cur != null) {
            sb.append(cur.val);
            cur = cur.next;
        }
        while (cur != null) {
            sb.append(", ").append(cur.val);
            cur = cur.next;
        }
        sb.append("]");
        System.out.println(sb.toString());
    }

    //Definition for singly-linked list.
    public static class ListNode {
        int val;
        ListNode next;

        ListNode(int x) {
            val = x;
        }
    }

}
