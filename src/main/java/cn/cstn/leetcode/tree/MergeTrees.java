package cn.cstn.leetcode.tree;

/**
 * 617              Merge Two Binary Trees
 * description :    Given two binary trees and imagine that when you put one of them to cover the other,
 *                  some nodes of the two trees are overlapped while the others are not.
 *                  You need to merge them into a new binary tree.
 *                  The merge rule is that if two nodes overlap,
 *                  then sum node values up as the new value of the merged node. Otherwise,
 *                  the NOT null node will be used as the node of new tree.
 * example 1:
 *                  Input:
 *                  	Tree 1                     Tree 2
 *                            1                         2
 *                           / \                       / \
 *                          3   2                     1   3
 *                         /                           \   \
 *                        5                             4   7
 *                  Output:
 *                  Merged tree:
 *                  	     3
 *                  	    / \
 *                  	   4   5
 *                  	  / \   \
 *                  	 5   4   7
 * note:            The merging process must start from the root nodes of both trees.
 * @author :       zhaohq
 * date :           2018-07-26 18:11
 */
public class MergeTrees {
    public static void main(String[] args) {
        TreeNode t1 = new TreeNode(1);
        TreeNode t2 = new TreeNode(3);
        TreeNode t3 = new TreeNode(2);
        TreeNode t4 = new TreeNode(5);
        t1.left = t2;
        t1.right = t3;
        t2.left = t4;
        TreeNode t5 = new TreeNode(2);
        TreeNode t6 = new TreeNode(1);
        TreeNode t7 = new TreeNode(3);
        TreeNode t8 = new TreeNode(4);
        TreeNode t9 = new TreeNode(7);
        t5.left = t6;
        t5.right = t7;
        t6.right = t8;
        t7.right = t9;

        TreeNode t = mergeTrees(t1, t5);
        t1.println("|- ");
        System.out.println();
        t5.println("|- ");
        System.out.println();
        t.println("|- ");
        System.out.println();
        t = _mergeTrees(t1, t5);
        t.println("|- ");
    }

    private static TreeNode _mergeTrees(TreeNode t1, TreeNode t2) {
        if (t1 == null) return t2;
        if (t2 == null) return t1;
        t1.val += t2.val;
        t1.left = mergeTrees(t1.left, t2.left);
        t1.right = mergeTrees(t1.right, t2.right);
        return t1;
    }

    private static TreeNode mergeTrees(TreeNode t1, TreeNode t2) {
        TreeNode t = null;
        if (t1 != null && t2 != null) {
            t = new TreeNode(t1.val + t2.val);
            t.left = mergeTrees(t1.left, t2.left);
            t.right = mergeTrees(t1.right, t2.right);
        } else if (t1 != null) {
            t = new TreeNode(t1.val);
            t.left = mergeTrees(t1.left, null);
            t.right = mergeTrees(t1.right, null);
        } else if (t2 != null) {
            t = new TreeNode(t2.val);
            t.left = mergeTrees(null, t2.left);
            t.right = mergeTrees(null, t2.right);
        }
        return t;
    }

    //Definition for a binary tree node.
    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }

        void println(String pad) {
            System.out.println(pad + val);
            if (left != null)
                left.println("  " + pad);
            if (right != null)
                right.println("  " + pad);
        }
    }
}
