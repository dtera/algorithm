package cn.cstn.algorithm.commons.list;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

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
    public ListNode random;

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

    public static ListNode copyRandomList(ListNode head) {
        if (head == null) return null;
        Map<ListNode, ListNode> visited = new HashMap<>();

        ListNode copyNode = new ListNode(head.val);
        visited.put(head, copyNode);
        for (ListNode node = head; node != null; node = node.next, copyNode = copyNode.next) {
            copyNode.next = getCopiedNode(node.next, visited);
            copyNode.random = getCopiedNode(node.random, visited);
        }

        return visited.get(head);
    }

    public static ListNode getCopiedNode(ListNode node, Map<ListNode, ListNode> visited) {
        if (node == null) return null;

        if (!visited.containsKey(node))
            visited.put(node, new ListNode(node.val));
        return visited.get(node);
    }

    public static ListNode copyRandomList_(ListNode head) {
        Map<ListNode, ListNode> visited = new HashMap<>();
        return copyRandomList_(head, visited);
    }

    public static ListNode copyRandomList_(ListNode node, Map<ListNode, ListNode> visited) {
        if (node == null) return null;
        if (visited.containsKey(node))
            return visited.get(node);

        ListNode copyNode = new ListNode(node.val);
        visited.put(node, copyNode);
        copyNode.next = copyRandomList_(node.next, visited);
        copyNode.random = copyRandomList_(node.random, visited);

        return copyNode;
    }

}