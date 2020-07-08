package cn.cstn.algorithm.offer;

import cn.cstn.algorithm.commons.Node;

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
        Node root = new Node(4);
        root.left = new Node(2);
        root.right = new Node(7);
        root.left.left = new Node(1);
        root.left.right = new Node(3);
        root.right.left = new Node(6);
        root.right.right = new Node(9);
        Node.print(root);
        System.out.println("===============================");
        Node node = Node.mirrorTree(root);
        Node.print(node);
    }

}
