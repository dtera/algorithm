package cn.cstn.algorithm.offer;

import cn.cstn.algorithm.commons.Node;

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
        int[] preOrder = {3, 9, 2, 1, 0, 8, 20, 15, 7};
        int[] inOrder = {0, 1, 2, 9, 8, 3, 15, 20, 7};
        Node tree = Node.buildTree(preOrder, inOrder);
        System.out.println(tree.toString(1));
        System.out.println("depth=" + Node.depth(tree));
        System.out.println("width=" + Node.width(tree));
        Node.print(tree);
        System.out.println("=========================================");
        Node.showTree(tree);
        System.out.println("=========================================");
        tree = Node._buildTree(preOrder, inOrder);
        System.out.println("=========================================");
        Node.print(tree);
    }

}
