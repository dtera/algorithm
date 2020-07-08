package cn.cstn.algorithm.offer;

import cn.cstn.algorithm.commons.Node;

/**
 * 请实现一个函数，用来判断一棵二叉树是不是对称的。如果一棵二叉树和它的镜像一样，那么它是对称的。
 * <p>
 * <p>
 * 例如，二叉树 [1,2,2,3,4,4,3] 是对称的。
 * <p>
 *     1
 * <p>
 *    / \
 * <p>
 *   2   2
 * <p>
 *  / \ / \
 * <p>
 * 3  4 4  3
 * <p>
 * <p>
 * 但是下面这个 [1,2,2,null,3,null,3] 则不是镜像对称的:
 * <p>
 *     1
 * <p>
 *    / \
 * <p>
 *   2   2
 * <p>
 *    \   \
 * <p>
 *    3    3
 * <p>
 *  
 * <p>
 * 示例 1：
 * <p>
 * 输入：root = [1,2,2,3,4,4,3]
 * <p>
 * 输出：true
 * <p>
 * <p>
 * 示例 2：
 * <p>
 * 输入：root = [1,2,2,null,3,null,3]
 * <p>
 * 输出：false
 *  
 * <p>
 * 限制：
 * <p>
 * 0 <= 节点个数 <= 1000
 * <p>
 *
 * @author zhaohuiqiang
 * @date 2020/6/27 17:25
 */
public class SymmetricTree {

    public static void main(String[] args) {
        Node root = new Node(1);
        root.left = new Node(2);
        root.right = new Node(2);
        root.left.left = new Node(3);
        root.left.right = new Node(4);
        root.right.left = new Node(4);
        root.right.right = new Node(3);
        Node.print(root);
        System.out.println(Node.isSymmetric(root));
    }
}
