package cn.cstn.algorithm.offer;

import lombok.extern.slf4j.Slf4j;

import java.util.Stack;

/**
 * 用两个栈实现一个队列。队列的声明如下，请实现它的两个函数 appendTail 和 deleteHead ，
 * 分别完成在队列尾部插入整数和在队列头部删除整数的功能。(若队列中没有元素，deleteHead 操作返回 -1 )
 * <p>
 *  
 * <p>
 * 示例 1：
 * <p>
 * 输入：
 * ["CQueue","appendTail","deleteHead","deleteHead"]
 * [[],[3],[],[]]
 * <p>
 * 输出：[null,null,3,-1]
 * <p>
 * 示例 2：
 * <p>
 * 输入：
 * ["CQueue","deleteHead","appendTail","appendTail","deleteHead","deleteHead"]
 * [[],[],[5],[2],[],[]]
 * <p>
 * 输出：[null,-1,null,null,5,2]
 * <p>
 * <p>
 * 提示：
 * <p>
 * 1 <= values <= 10000
 * 最多会对 appendTail、deleteHead 进行 10000 次调用
 *
 * @author zhaohuiqiang
 * @date 2020/6/1 18:49
 */
@Slf4j
public class CQueue {

    public static void main(String[] args) {
        CQueue obj = new CQueue();
        obj.appendTail(1);
        obj.appendTail(2);
        obj.appendTail(3);
        obj.appendTail(4);
        int head = obj.deleteHead();
        System.out.println(head);
    }

    private final Stack<Integer> a;
    private final Stack<Integer> b;

    public CQueue() {
        a = new Stack<>();
        b = new Stack<>();
    }

    public void appendTail(int value) {
        while (!a.empty()) b.push(a.pop());
        a.push(value);
        while (!b.empty()) a.push(b.pop());
    }

    public int deleteHead() {
        if (a.empty()) return -1;
        return a.pop();
    }

}
