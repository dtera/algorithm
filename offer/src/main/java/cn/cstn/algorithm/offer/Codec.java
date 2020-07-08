package cn.cstn.algorithm.offer;

import cn.cstn.algorithm.commons.Node;

/**
 * 请实现两个函数，分别用来序列化和反序列化二叉树。
 * <p>
 * <p>
 * 示例: 
 * <p>
 * 你可以将以下二叉树：
 * <p>
 * 1
 * <p>
 * / \
 * <p>
 * 2   3
 * <p>
 * &nbsp;&nbsp;/ \
 * <p>
 * &nbsp;&nbsp;4   5
 * <p>
 * 序列化为 "[1,2,3,null,null,4,5]"
 * <p>
 *
 * @author zhaohuiqiang
 * @date 2020/7/3 14:56
 */
public class Codec {

    public static void main(String[] args) {
        Node root = new Node(1);
        root.left = new Node(2);
        root.right = new Node(3);
        root.right.left = new Node(4);
        root.right.right = new Node(5);

        String s = Node.serialize(root);
        System.out.println(s);
        Node treeNode = Node.deserialize(s);
        Node.showTree(treeNode);

    }

}
