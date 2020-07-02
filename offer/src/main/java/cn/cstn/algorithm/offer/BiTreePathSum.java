package cn.cstn.algorithm.offer;

import cn.cstn.algorithm.commons.tree.BiTreeNode;

import java.util.List;

/**
 * 输入一棵二叉树和一个整数，打印出二叉树中节点值的和为输入整数的所有路径。从树的根节点开始往下一直到叶节点所经过的节点形成一条路径。
 * <p>
 *  
 * <p>
 * 示例:
 * <p>
 * 给定如下二叉树，以及目标和 sum = 22，
 * <p>
 * &nbsp;&nbsp;&nbsp;5
 * <p>
 * &nbsp;/&nbsp;&nbsp;&nbsp;&nbsp;\
 * <p>
 * &nbsp;4&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;8
 * <p>
 * &nbsp;/&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;/&nbsp;\
 * <p>
 * 11&nbsp;&nbsp;13&nbsp;4
 * <p>
 * /&nbsp;\&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;/ \
 * <p>
 * 7&nbsp;2&nbsp;&nbsp;&nbsp;&nbsp;5   1
 * <p>
 * 返回:
 * <p>
 * [
 * <p>
 * [5,4,11,2],
 * <p>
 * [5,8,4,5]
 * <p>
 * ]
 *  
 * <p>
 * 提示：
 * <p>
 * 节点总数 <= 10000
 *
 * @author zhaohuiqiang
 * @date 2020/7/2 16:29
 */
public class BiTreePathSum {

    public static void main(String[] args) {
        BiTreeNode root = new BiTreeNode(5);
        root.left = new BiTreeNode(4);
        root.right = new BiTreeNode(8);
        root.left.left = new BiTreeNode(11);
        root.right.left = new BiTreeNode(13);
        root.right.right = new BiTreeNode(4);
        root.left.left.left = new BiTreeNode(7);
        root.left.left.right = new BiTreeNode(2);
        root.right.right.left = new BiTreeNode(5);
        root.right.right.right = new BiTreeNode(1);

        List<List<Integer>> pathSum = BiTreeNode.pathSum(root, 22);
        System.out.println(pathSum);
    }

}
