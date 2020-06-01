package cn.cstn.algorithm.offer;

import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

/**
 * 输入某二叉树的前序遍历和中序遍历的结果，请重建该二叉树。假设输入的前序遍历和中序遍历的结果中都不含重复的数字。
 * <p>
 *  
 * <p>
 * 例如，给出
 * <p>
 * 前序遍历 preorder = [3,9,20,15,7]
 * <p>
 * 中序遍历 inorder = [9,3,15,20,7]
 * <p>
 * 返回如下的二叉树：
 * <p>
 * &nbsp;&nbsp;3
 * <p>
 * /&nbsp;&nbsp;\
 * <p>
 * 9&nbsp;&nbsp;20
 * <p>
 * &nbsp;&nbsp;&nbsp;&nbsp;/&nbsp;&nbsp;\
 * <p>
 * &nbsp;&nbsp;15&nbsp;&nbsp;7
 *  
 * <p>
 * 限制：
 * <p>
 * 0 <= 节点个数 <= 5000
 *
 * @author zhaohuiqiang
 * @date 2020/5/29 16:47
 */
public class BuildTree {

    public static void main(String[] args) {
        int[] preorder = {3, 9, 2, 8, 20, 15, 7};
        int[] inorder = {2, 9, 8, 3, 15, 20, 7};
        TreeNode tree = buildTree(preorder, inorder);
        System.out.println(tree);
        System.out.println("depth=" + depth(tree));
        print(tree);
        System.out.println("=========================================");
        tree = _buildTree(preorder, inorder);
        print(tree);
    }

    public static TreeNode _buildTree(int[] preorder, int[] inorder) {
        if (preorder == null || preorder.length == 0) return null;

        int inorderIndex = 0;
        TreeNode root = new TreeNode(preorder[0]);
        Stack<TreeNode> stack = new Stack<TreeNode>() {{
            push(root);
        }};
        for (int i = 1; i < preorder.length; i++) {
            TreeNode cur = new TreeNode(preorder[i]);
            TreeNode node = stack.peek();
            if (node.val != inorder[inorderIndex]) {
                node.left = cur;
                stack.add(node.left);
            } else {
                while (!stack.isEmpty() && stack.peek().val == inorder[inorderIndex]) {
                    node = stack.pop();
                    inorderIndex++;
                }
                node.right = cur;
                stack.add(node.right);
            }
        }
        return root;
    }

    public static TreeNode buildTree(int[] preorder, int[] inorder) {
        if (preorder == null || preorder.length == 0) return null;

        Map<Integer, Integer> inorderMap = new HashMap<>();
        for (int i = 0; i < inorder.length; i++) {
            inorderMap.put(inorder[i], i);
        }
        return buildTree(preorder, 0, preorder.length - 1, 0, inorder.length - 1,
                inorderMap);
    }

    private static TreeNode buildTree(int[] preorder, int preStart, int preEnd, int inStart, int inEnd,
                                      Map<Integer, Integer> inorderMap) {
        if (preStart > preEnd) return null;
        int rootVal = preorder[preStart];
        TreeNode root = new TreeNode(rootVal);
        int rootIndex = inorderMap.get(rootVal);
        if (preStart == preEnd) {
            return root;
        }

        int leftNodes = rootIndex - inStart, rightNodes = inEnd - rootIndex;
        root.left = buildTree(preorder, preStart + 1, preStart + leftNodes,
                inStart, rootIndex - 1, inorderMap);
        root.right = buildTree(preorder, preEnd - rightNodes + 1, preEnd,
                rootIndex + 1, inEnd, inorderMap);

        return root;
    }

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
                System.out.print(repeat(sep, node.getLeft() - offset) + " " + node.getRight().val);

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

    public static int depth(TreeNode tree) {
        if (tree == null) return 0;
        return Math.max(depth(tree.left), depth(tree.left)) + 1;
    }

    private static String repeat(String sep, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(sep);
        }
        return sb.toString();
    }

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }

        @Override
        public String toString() {
            return "TreeNode{" +
                    "val=" + val +
                    ", left=" + (left == null ? "null" : left.toString()) +
                    ", right=" + (right == null ? "null" : right.toString()) +
                    "}";
        }
    }

}
