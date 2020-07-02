package cn.cstn.algorithm.offer;

import cn.cstn.algorithm.commons.tree.BiTreeNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

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
 * <p>
 *
 * @author zhaohuiqiang
 * @date 2020/5/29 16:47
 */
public class BuildTree {

    public static void main(String[] args) {
        int[] preorder = {3, 9, 2, 1, 0, 8, 20, 15, 7};
        int[] inorder = {0, 1, 2, 9, 8, 3, 15, 20, 7};
        BiTreeNode tree = buildTree(preorder, inorder);
        System.out.println(tree.toString(1));
        System.out.println("depth=" + BiTreeNode.depth(tree));
        System.out.println("width=" + BiTreeNode.width(tree));
        BiTreeNode.print(tree);
        System.out.println("=========================================");
        BiTreeNode.show(tree);
        System.out.println("=========================================");
        tree = _buildTree(preorder, inorder);
        System.out.println("=========================================");
        BiTreeNode.print(tree);
    }

    public static BiTreeNode _buildTree(int[] preorder, int[] inorder) {
        if (preorder == null || preorder.length == 0) return null;

        int inorderIndex = 0;
        BiTreeNode root = new BiTreeNode(preorder[0]);
        Stack<BiTreeNode> stack = new Stack<BiTreeNode>() {{
            push(root);
        }};
        for (int i = 1; i < preorder.length; i++) {
            BiTreeNode cur = new BiTreeNode(preorder[i]);
            BiTreeNode node = stack.peek();
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

    public static BiTreeNode buildTree(int[] preorder, int[] inorder) {
        if (preorder == null || preorder.length == 0) return null;

        Map<Integer, Integer> inorderMap = new HashMap<>();
        for (int i = 0; i < inorder.length; i++) {
            inorderMap.put(inorder[i], i);
        }
        return buildTree(preorder, 0, preorder.length - 1, 0, inorder.length - 1,
                inorderMap);
    }

    private static BiTreeNode buildTree(int[] preorder, int preStart, int preEnd, int inStart, int inEnd,
                                        Map<Integer, Integer> inorderMap) {
        if (preStart > preEnd) return null;
        int rootVal = preorder[preStart];
        BiTreeNode root = new BiTreeNode(rootVal);
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


}
