package cn.cstn.algorithm.commons.tree;

import cn.cstn.algorithm.commons.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * TreeNode
 *
 * @author zhaohuiqiang
 * @date 2020/6/26 10:27
 */
@RequiredArgsConstructor
public class BiTreeNode {
    public final int val;
    public BiTreeNode left;
    public BiTreeNode right;

    public static void print(BiTreeNode tree) {
        if (tree == null) {
            return;
        }
        Queue<Pair<Integer, BiTreeNode>> queue = new LinkedList<Pair<Integer, BiTreeNode>>() {{
            add(Pair.of(depth(tree) - 1, tree));
        }};
        while (!queue.isEmpty()) {
            int offset = 0;
            for (int i = queue.size(); i > 0; i--) {
                Pair<Integer, BiTreeNode> node = queue.poll();
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

    public static void show(BiTreeNode tree) {
        show(null, tree, "|");
    }

    public static void show(BiTreeNode parent, BiTreeNode tree, String prefix) {
        if (tree == null) return;

        System.out.println(prefix + "--" + tree.val);
        if (parent == null || parent.right == null || parent.right == tree) {
            prefix = prefix.substring(0, prefix.length() - 1) + " ";
        }
        show(tree, tree.left, prefix + "  |");
        show(tree, tree.right, prefix + "  |");
    }

    public static int width(BiTreeNode tree) {
        if (tree == null) return 0;
        return width(tree.left) + width(tree.right) + 1;
    }

    public static int depth(BiTreeNode tree) {
        if (tree == null) return 0;
        return Math.max(depth(tree.left), depth(tree.right)) + ("" + tree.val).length();
    }

    public static List<List<Integer>> pathSum(BiTreeNode root, int sum) {
        List<List<Integer>> res = new ArrayList<>();
        LinkedList<Integer> path = new LinkedList<>();
        dfs(root, sum, res, path);
        return res;
    }

    private static void dfs(BiTreeNode root, int sum, List<List<Integer>> res, LinkedList<Integer> path) {
        if (root == null) return;
        sum -= root.val;
        path.add(root.val);
        if (sum == 0 && root.left == null && root.right == null) {
            res.add(new ArrayList<>(path));
        }
        dfs(root.left, sum, res, path);
        dfs(root.right, sum, res, path);
        path.removeLast();
    }

    public static boolean verifyPostOrder(int[] postOrder) {
        if (postOrder == null) return false;
        return verifyPostOrder(postOrder, 0, postOrder.length - 1);
    }

    private static boolean verifyPostOrder(int[] postOrder, int i, int j) {
        if (i >= j) return true;
        int k = i;
        while (postOrder[k] < postOrder[j]) k++;
        int m = k;
        while (postOrder[k] > postOrder[j]) k++;

        return k == j && verifyPostOrder(postOrder, i, m - 1) && verifyPostOrder(postOrder, m, j - 1);
    }

    public static List<List<Integer>> levelOrderII(BiTreeNode root, boolean cross) {
        if (root == null) return new ArrayList<>();
        Queue<BiTreeNode> queue = new LinkedList<BiTreeNode>() {{
            add(root);
        }};
        List<List<Integer>> res = new ArrayList<>();

        int k = 1;
        while (!queue.isEmpty()) {
            LinkedList<Integer> tmp = new LinkedList<>();
            for (int i = queue.size(); i > 0; i--) {
                BiTreeNode node = queue.poll();
                assert node != null;
                if (cross && k % 2 == 0) tmp.addFirst(node.val);
                else tmp.add(node.val);
                if (node.left != null) queue.add(node.left);
                if (node.right != null) queue.add(node.right);
            }
            res.add(tmp);
            k++;
        }

        return res;
    }

    public static int[] levelOrderI(BiTreeNode root) {
        if (root == null) return new int[0];
        Queue<BiTreeNode> queue = new LinkedList<BiTreeNode>() {{
            add(root);
        }};
        List<Integer> list = new ArrayList<>();

        while (!queue.isEmpty()) {
            BiTreeNode node = queue.poll();
            list.add(node.val);
            if (node.left != null) queue.add(node.left);
            if (node.right != null) queue.add(node.right);
        }

        int[] res = new int[list.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = list.get(i);
        }

        return res;
    }

    public static boolean isSymmetric(BiTreeNode root) {
        return root == null || isSymmetric(root.left, root.right);
    }

    private static boolean isSymmetric(BiTreeNode left, BiTreeNode right) {
        if (left == null && right == null) return true;
        if (left == null || right == null || left.val != right.val) return false;

        return isSymmetric(left.left, right.right) && isSymmetric(left.right, right.left);
    }

    public static BiTreeNode mirrorTree(BiTreeNode root) {
        if (root == null) return null;

        BiTreeNode node = new BiTreeNode(root.val);
        node.left = mirrorTree(root.right);
        node.right = mirrorTree(root.left);
        return node;
    }

    public static boolean isSubStructure(BiTreeNode A, BiTreeNode B) {
        return A != null && B != null && (subStructure(A, B) ||
                isSubStructure(A.left, B) || isSubStructure(A.right, B));
    }

    private static boolean subStructure(BiTreeNode a, BiTreeNode b) {
        if (b == null) return true;
        if (a == null || a.val != b.val) return false;

        return subStructure(a.left, b.left) && subStructure(a.right, b.right);
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