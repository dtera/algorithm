package cn.cstn.algorithm.offer;

import cn.cstn.algorithm.commons.Node;
import cn.cstn.algorithm.commons.util.ArrayUtil;

import java.util.List;

/**
 * BiTreeLevelOrder
 * <p>
 *
 * @author zhaohuiqiang
 * @date 2020/6/29 17:29
 */
public class BiTreeLevelOrder {

    public static void main(String[] args) {
        Node root = new Node(3);
        root.left = new Node(9);
        root.right = new Node(20);
        root.right.left = new Node(15);
        root.right.right = new Node(7);

        levelOrderI(root);
        levelOrderII(root);
        levelOrderIII(root);
    }

    /**
     * 请实现一个函数按照之字形顺序打印二叉树，即第一行按照从左到右的顺序打印，第二层按照从右到左的顺序打印，
     * 第三行再按照从左到右的顺序打印，其他行以此类推。
     * <p>
     *  
     * <p>
     * 例如:
     * <p>
     * 给定二叉树: [3,9,20,null,null,15,7],
     * <p>
     * 3
     * <p>
     * / \
     * <p>
     * 9  20
     * <p>
     * &nbsp;&nbsp;&nbsp;/  \
     * <p>
     * &nbsp;&nbsp;15   7
     * <p>
     * <p>
     * 返回其层次遍历结果：
     * <p>
     * [
     * <p>
     * &nbsp;&nbsp;[3],
     * <p>
     * &nbsp;&nbsp;[20,9],
     * <p>
     * &nbsp;&nbsp;[15,7]
     * <p>
     * ]
     * <p>
     *
     * @param root root
     */
    private static void levelOrderIII(Node root) {
        List<List<Integer>> res = Node.levelOrderII(root, true);
        System.out.println(res);
    }

    /**
     * 从上到下打印出二叉树的每个节点，同一层的节点按照从左到右的顺序打印。
     * <p>
     *  
     * <p>
     * 例如:
     * <p>
     * 给定二叉树: [3,9,20,null,null,15,7],
     * <p>
     * 3
     * <p>
     * / \
     * <p>
     * 9  20
     * <p>
     * &nbsp;&nbsp;&nbsp;/  \
     * <p>
     * &nbsp;&nbsp;15   7
     * <p>
     * <p>
     * 返回其层次遍历结果：
     * <p>
     * [
     * <p>
     * &nbsp;&nbsp;[3],
     * <p>
     * &nbsp;&nbsp;[9,20],
     * <p>
     * &nbsp;&nbsp;[15,7]
     * <p>
     * ]
     * <p>
     *
     * @param root root
     */
    private static void levelOrderII(Node root) {
        List<List<Integer>> res = Node.levelOrderII(root, false);
        System.out.println(res);
    }

    /**
     * 从上到下打印出二叉树的每个节点，同一层的节点按照从左到右的顺序打印。
     * <p>
     *  
     * <p>
     * 例如:
     * <p>
     * 给定二叉树: [3,9,20,null,null,15,7],
     * <p>
     * 3
     * <p>
     * / \
     * <p>
     * 9  20
     * <p>
     * &nbsp;&nbsp;&nbsp;/  \
     * <p>
     * &nbsp;&nbsp;15   7
     * <p>
     * <p>
     * 返回：
     * <p>
     * [3,9,20,15,7]
     * <p>
     *
     * @param root root
     */
    private static void levelOrderI(Node root) {
        int[] arr = Node.levelOrderI(root);
        ArrayUtil.println(arr);
    }

}
