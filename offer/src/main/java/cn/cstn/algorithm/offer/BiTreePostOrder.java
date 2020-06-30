package cn.cstn.algorithm.offer;

import cn.cstn.algorithm.commons.tree.TreeNode;

/**
 * 输入一个整数数组，判断该数组是不是某二叉搜索树的后序遍历结果。如果是则返回 true，否则返回 false。
 * 假设输入的数组的任意两个数字都互不相同。
 * <p>
 * <p>
 * 参考以下这颗二叉搜索树：
 * <p>
 * 5
 * <p>
 * / \
 * <p>
 * 2   6
 * <p>
 * / \
 * <p>
 * 1   3
 * <p>
 * <p>
 * 示例 1：
 * <p>
 * 输入: [1,6,3,2,5]
 * <p>
 * 输出: false
 * <p>
 * <p>
 * 示例 2：
 * <p>
 * 输入: [1,3,2,6,5]
 * <p>
 * 输出: true
 *  
 * <p>
 * 提示：
 * <p>
 * 数组长度 <= 1000
 * <p>
 *
 * @author zhaohuiqiang
 * @date 2020/6/30 9:00
 */
public class BiTreePostOrder {

    public static void main(String[] args) {
        int[] postOrder = {1, 6, 3, 2, 5};
        System.out.println(TreeNode.verifyPostOrder(postOrder));
        postOrder = new int[]{1, 3, 2, 6, 5};
        System.out.println(TreeNode.verifyPostOrder(postOrder));

    }

}
