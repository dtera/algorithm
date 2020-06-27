package cn.cstn.algorithm.offer;

import cn.cstn.algorithm.commons.tree.TreeNode;

/**
 * 请完成一个函数，输入一个二叉树，该函数输出它的镜像。
 * <p>
 * <p>
 * 例如输入：
 * <p>
 *      4
 * <p>
 *    /   \
 * <p>
 *   2     7
 * <p>
 *  / \   / \
 * <p>
 * 1   3 6   9
 * <p>
 * 镜像输出：
 * <p>
 *      4
 * <p>
 *    /   \
 * <p>
 *   7     2
 * <p>
 *  / \   / \
 * <p>
 * 9   6 3   1
 * <p>
 *  
 * <p>
 * 示例 1：
 * <p>
 * 输入：root = [4,2,7,1,3,6,9]
 * <p>
 * 输出：[4,7,2,9,6,3,1]
 *  
 * <p>
 * 限制：
 * <p>
 * 0 <= 节点个数 <= 1000
 * <p>
 *
 * @author zhaohuiqiang
 * @date 2020/6/27 16:36
 */
public class MirrorTree {

    public static void main(String[] args) {
        TreeNode root = new TreeNode(4);
        root.left = new TreeNode(2);
        root.right = new TreeNode(7);
        root.left.left = new TreeNode(1);
        root.left.right = new TreeNode(3);
        root.right.left = new TreeNode(6);
        root.right.right = new TreeNode(9);
        TreeNode.print(root);
        System.out.println("===============================");
        TreeNode node = TreeNode.mirrorTree(root);
        TreeNode.print(node);
    }

}
