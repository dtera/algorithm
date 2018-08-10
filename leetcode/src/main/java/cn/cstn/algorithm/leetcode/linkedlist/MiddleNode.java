package cn.cstn.algorithm.leetcode.linkedlist;

/**
 * 876                  Middle of the Linked List
 * description :        Given a non-empty, singly linked list with head node head,
 *                      return a middle node of linked list.
 *                      If there are two middle nodes, return the second middle node.
 * example 1 :
 *                      Input: [1,2,3,4,5]
 *                      Output: Node 3 from this list (Serialization: [3,4,5])
 *                      The returned node has value 3.  (The judge's serialization of this node is [3,4,5]).
 *                      Note that we returned a ListNode object ans, such that:
 *                      ans.val = 3, ans.next.val = 4, ans.next.next.val = 5, and ans.next.next.next = NULL.
 * example 2 :
 *                      Input: [1,2,3,4,5,6]
 *                      Output: Node 4 from this list (Serialization: [4,5,6])
 *                      Since the list has two middle nodes with values 3 and 4, we return the second one.
 * note :
 *                      The number of nodes in the given list will be between 1 and 100.
 *
 * @author :           zhaohq
 * date :               2018/7/29 20:38
 */
public class MiddleNode {
    public static void main(String[] args) {
        ListNode n1 = new ListNode(1);
        ListNode n2 = new ListNode(2);
        ListNode n3 = new ListNode(3);
        ListNode n4 = new ListNode(4);
        ListNode n5 = new ListNode(5);
        ListNode n6 = new ListNode(6);
        n1.next = n2;
        n2.next = n3;
        n3.next = n4;
        n4.next = n5;
        n5.next = n6;
        println(n1);
        ListNode middleNode = middleNode(n1);
        println(middleNode);
    }

    private static ListNode middleNode(ListNode head) {
        ListNode node = head;
        int count = 0;
        while (node != null) {
            count++;
            node = node.next;
        }
        node = head;
        int mid = count / 2;
        for (int i = 0; i < mid; i++) {
            node = node.next;
        }
        return node;
    }

    private static void println(ListNode node) {
        if (node == null) return;
        System.out.print("[" + node.val);
        while (node.next != null) {
            System.out.print(", " + node.next.val);
            node = node.next;
        }
        System.out.println("]");
    }

    //Definition for singly-linked list.
    static class ListNode {
        int val;
        ListNode next;

        ListNode(int x) {
            val = x;
        }
    }
}
