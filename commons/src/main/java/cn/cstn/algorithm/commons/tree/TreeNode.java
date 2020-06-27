package cn.cstn.algorithm.commons.tree;

import cn.cstn.algorithm.commons.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;
import java.util.Queue;

/**
 * TreeNode
 *
 * @author zhaohuiqiang
 * @date 2020/6/26 10:27
 */
@RequiredArgsConstructor
public class TreeNode {
    public final int val;
    public TreeNode left;
    public TreeNode right;

    public static void print(TreeNode tree) {
        if (tree == null) {
            return;
        }
        Queue<Pair<Integer, TreeNode>> queue = new LinkedList<Pair<Integer, TreeNode>>() {{
            add(Pair.of(depth(tree) - 1, tree));
        }};
        while (!queue.isEmpty()) {
            int offset = 0;
            for (int i = queue.size(); i > 0; i--) {
                Pair<Integer, TreeNode> node = queue.poll();
                assert node != null;
                String sep = "\t";
                System.out.print(StringUtil.repeat(sep, node.getLeft() - offset) + " " + node.getRight().val);

                if (node.getRight().left != null) {
                    queue.add(Pair.of(node.getLeft() - 1, node.getRight().left));
                }
                if (node.getRight().right != null) {
                    queue.add(Pair.of(node.getLeft() + 1, node.getRight().right));
                }
                offset = node.getLeft();
            }
            System.out.println();
        }
    }

    public static void show(TreeNode tree) {
        show(null, tree, "|");
    }

    public static void show(TreeNode parent, TreeNode tree, String prefix) {
        if (tree == null) return;

        System.out.println(prefix + "--" + tree.val);
        if (parent == null || parent.right == null || parent.right == tree) {
            prefix = prefix.substring(0, prefix.length() - 1) + " ";
        }
        show(tree, tree.left, prefix + "  |");
        show(tree, tree.right, prefix + "  |");
    }

    public static int width(TreeNode tree) {
        if (tree == null) return 0;
        return width(tree.left) + width(tree.right) + 1;
    }

    public static int depth(TreeNode tree) {
        if (tree == null) return 0;
        return Math.max(depth(tree.left), depth(tree.right)) + ("" + tree.val).length();
    }

    public static boolean isSubStructure(TreeNode A, TreeNode B) {
        return A != null && B != null && (dfs(A, B) ||
                isSubStructure(A.left, B) || isSubStructure(A.right, B));
    }

    private static boolean dfs(TreeNode a, TreeNode b) {
        if (b == null) return true;
        if (a == null || a.val != b.val) return false;

        return dfs(a.left, b.left) && dfs(a.right, b.right);
    }

    public String toString(int layer) {
        return "{" +
                "\n" + StringUtil.repeat("\t", layer) + "val: " + val +
                ",\n" + StringUtil.repeat("\t", layer) + "left: " +
                (left == null ? "null" : left.toString(layer + 1)) +
                ",\n" + StringUtil.repeat("\t", layer) + "right: " +
                (right == null ? "null" : right.toString(layer + 1)) +
                "\n" + StringUtil.repeat("\t", layer - 1) + "}";
    }

    @Override
    public String toString() {
        return "TreeNode{" +
                "val=" + val +
                ", left=" + left +
                ", right=" + right +
                '}';
    }

}