package cn.cstn.algorithm.offer;

import cn.cstn.algorithm.commons.list.ListNode;

/**
 * 请实现 copyRandomList 函数，复制一个复杂链表。在复杂链表中，每个节点除了有一个 next 指针指向下一个节点，
 * 还有一个 random 指针指向链表中的任意节点或者 null。
 * <p>
 * <p>
 * 示例 1：
 * <p>
 * 输入：head = [[7,null],[13,0],[11,4],[10,2],[1,0]]
 * <p>
 * 输出：[[7,null],[13,0],[11,4],[10,2],[1,0]]
 * <p>
 * <p>
 * 示例 2：
 * <p>
 * 输入：head = [[1,1],[2,1]]
 * <p>
 * 输出：[[1,1],[2,1]]
 * <p>
 * <p>
 * 示例 3：
 * <p>
 * 输入：head = [[3,null],[3,0],[3,null]]
 * <p>
 * 输出：[[3,null],[3,0],[3,null]]
 * <p>
 * <p>
 * 示例 4：
 * <p>
 * 输入：head = []
 * <p>
 * 输出：[]
 * <p>
 * <p>
 * 解释：给定的链表为空（空指针），因此返回 null。
 * <p>
 * <p>
 * 提示：
 * <p>
 * -10000 <= Node.val <= 10000
 * <p>
 * Node.random 为空（null）或指向链表中的节点。
 * <p>
 * 节点数目不超过 1000 。
 * <p>
 *
 * @author zhaohuiqiang
 * @date 2020/7/2 17:34
 */
public class CopyRandomList {

    public static void main(String[] args) {
        ListNode head = new ListNode(7);
        head.next = new ListNode(13);
        head.next.next = new ListNode(11);
        head.next.next.next = new ListNode(10);
        head.next.next.next.next = new ListNode(1);
        head.next.random = head;
        head.next.next.random = head.next.next.next.next;
        head.next.next.next.random = head.next.next;
        head.next.next.next.next.random = head;
        head.println();
        ListNode.copyRandomList(head).println();
        ListNode.copyRandomList_(head).println();
    }

}
