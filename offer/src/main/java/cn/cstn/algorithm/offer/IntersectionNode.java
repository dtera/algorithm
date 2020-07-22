package cn.cstn.algorithm.offer;

import cn.cstn.algorithm.commons.list.ListNode;

/**
 * 输入两个链表，找出它们的第一个公共节点。
 * <p>
 * 如下面的两个链表：
 * <p>
 * <p>
 * <p>
 * 在节点 c1 开始相交。
 * <p>
 *  
 * <p>
 * 示例 1：
 * <p>
 * 输入：intersectVal = 8, listA = [4,1,8,4,5], listB = [5,0,1,8,4,5], skipA = 2, skipB = 3
 * <p>
 * 输出：Reference of the node with value = 8
 * <p>
 * 输入解释：相交节点的值为 8 （注意，如果两个列表相交则不能为 0）。从各自的表头开始算起，链表 A 为 [4,1,8,4,5]，
 * 链表 B 为 [5,0,1,8,4,5]。在 A 中，相交节点前有 2 个节点；在 B 中，相交节点前有 3 个节点。
 * <p> 
 * <p>
 * 示例 2：
 * <p>
 * 输入：intersectVal = 2, listA = [0,9,1,2,4], listB = [3,2,4], skipA = 3, skipB = 1
 * <p>
 * 输出：Reference of the node with value = 2
 * <p>
 * 输入解释：相交节点的值为 2 （注意，如果两个列表相交则不能为 0）。从各自的表头开始算起，链表 A 为 [0,9,1,2,4]，链表 B 为 [3,2,4]。
 * <p>
 * 在 A 中，相交节点前有 3 个节点；在 B 中，相交节点前有 1 个节点。
 * <p> 
 * <p>
 * 示例 3：
 * <p>
 * 输入：intersectVal = 0, listA = [2,6,4], listB = [1,5], skipA = 3, skipB = 2
 * <p>
 * 输出：null
 * <p>
 * 输入解释：从各自的表头开始算起，链表 A 为 [2,6,4]，链表 B 为 [1,5]。由于这两个链表不相交，所以 intersectVal 必须为 0，
 * 而 skipA 和 skipB 可以是任意值。
 * <p>
 * 解释：这两个链表不相交，因此返回 null。
 * <p>
 * <p>
 * 注意：
 * <p>
 * 如果两个链表没有交点，返回 null.
 * 在返回结果后，两个链表仍须保持原有的结构。
 * 可假定整个链表结构中没有循环。
 * 程序尽量满足 O(n) 时间复杂度，且仅用 O(1) 内存。
 * 本题与主站 160 题相同：https://leetcode-cn.com/problems/intersection-of-two-linked-lists/
 * <p>
 *
 * @author zhaohuiqiang
 * @date 2020/7/22 17:59
 */
public class IntersectionNode {

    public static void main(String[] args) {
        ListNode headA = new ListNode(0);
        ListNode headB = new ListNode(3);

        ListNode node = new ListNode(2);
        node.next = new ListNode(4);

        headA.next = new ListNode(9);
        headA.next.next = new ListNode(1);
        headA.next.next.next = node;
        headB.next = node;

        headA.println();
        headB.println();
        ListNode.getIntersectionNode(headA, headB).println();
    }

}
