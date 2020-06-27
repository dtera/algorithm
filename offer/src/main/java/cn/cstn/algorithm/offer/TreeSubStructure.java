package cn.cstn.algorithm.offer;

import cn.cstn.algorithm.commons.tree.TreeNode;

/**
 * 输入两棵二叉树A和B，判断B是不是A的子结构。(约定空树不是任意一个树的子结构)
 * <p>
 * B是A的子结构， 即 A中有出现和B相同的结构和节点值。
 * <p>
 * <p>
 * 例如:
 * <p>
 * 给定的树 A:
 * <p>
 *      3
 * <p>
 *     / \
 * <p>
 *    4   5
 * <p>
 *   / \
 * <p>
 *  1   2
 * <p>
 * <p>
 * 给定的树 B：
 * <p>
 *    4 
 * <p>
 *   /
 * <p>
 *  1
 * <p>
 * 返回 true，因为 B 与 A 的一个子树拥有相同的结构和节点值。
 * <p>
 * <p>
 * 示例 1：
 * <p>
 * 输入：A = [1,2,3], B = [3,1]
 * <p>
 * 输出：false
 * <p>
 * <p>
 * 示例 2：
 * <p>
 * 输入：A = [3,4,5,1,2], B = [4,1]
 * <p>
 * 输出：true
 * <p>
 * <p>
 * 限制：
 * <p>
 * 0 <= 节点个数 <= 10000
 * <p>
 *
 * @author zhaohuiqiang
 * @date 2020/6/26 10:21
 */
public class TreeSubStructure {

    public static void main(String[] args) {
        TreeNode A = new TreeNode(3);
        A.left = new TreeNode(4);
        A.right = new TreeNode(5);
        A.left.left = new TreeNode(1);
        A.left.right = new TreeNode(2);

        TreeNode B = new TreeNode(4);
        B.left = new TreeNode(1);
        System.out.println(TreeNode.isSubStructure(A, B));
    }

}
