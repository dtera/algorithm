package cn.cstn.algorithm.leetcode.linkedlist;

public class AddTwoNumbers {
    public static class ListNode {
        int val;
        ListNode next;

        ListNode(int val) {
            this.val = val;
        }

        ListNode(int val, ListNode next) {
            this.val = val;
            this.next = next;
        }

        @Override
        public String toString() {
            if (next == null) return "" + val;
            return next.toString() + val;
        }


    }

    public static ListNode addCarryNumbers(int carry, ListNode l) {
        if (carry == 0) {
            return l;
        } else {
            return addTwoNumbers(0, new ListNode(carry), l);
        }
    }

    public static ListNode addTwoNumbers(int carry, ListNode l1, ListNode l2) {
        if (l1 == null) {
            return addCarryNumbers(carry, l2);
        }
        if (l2 == null) {
            return addCarryNumbers(carry, l1);
        }

        int val = (l1.val + l2.val + carry) % 10;
        carry = (l1.val + l2.val + carry) / 10;
        ListNode result = new ListNode(val);
        result.next = addTwoNumbers(carry, l1.next, l2.next);

        return result;
    }

    public static ListNode addTwoNumbers_(ListNode l1, ListNode l2) {
        return addTwoNumbers(0, l1, l2);
    }

    public static ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode dummyHead = new ListNode(0);
        ListNode curr = dummyHead;
        int carry = 0;
        while (l1 != null || l2 != null || carry != 0) {
            int x = (l1 != null) ? l1.val : 0;
            int y = (l2 != null) ? l2.val : 0;
            int sum = carry + x + y;
            carry = sum / 10;
            curr.next = new ListNode(sum % 10);
            curr = curr.next;
            if (l1 != null) l1 = l1.next;
            if (l2 != null) l2 = l2.next;
        }
        return dummyHead.next;
    }

    public static void main(String[] args) {
        ListNode l10 = new ListNode(3);
        ListNode l11 = new ListNode(4, l10);
        ListNode l12 = new ListNode(2, l11);
        System.out.println(l12);

        ListNode l20 = new ListNode(4);
        ListNode l21 = new ListNode(6, l20);
        ListNode l22 = new ListNode(5, l21);
        System.out.println(l22);

        System.out.println(addTwoNumbers_(l12, l22));
        System.out.println(addTwoNumbers(l12, l22));
    }
}
