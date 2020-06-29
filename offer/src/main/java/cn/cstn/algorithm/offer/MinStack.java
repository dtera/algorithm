package cn.cstn.algorithm.offer;

import java.util.Stack;

/**
 * 定义栈的数据结构，请在该类型中实现一个能够得到栈的最小元素的 min 函数在该栈中，调用 min、push 及 pop 的时间复杂度都是 O(1)。
 * <p>
 * <p>
 * 示例:
 * <p>
 * MinStack minStack = new MinStack();
 * <p>
 * minStack.push(-2);
 * <p>
 * minStack.push(0);
 * <p>
 * minStack.push(-3);
 * <p>
 * minStack.min();   --> 返回 -3.
 * <p>
 * minStack.pop();
 * <p>
 * minStack.top();      --> 返回 0.
 * <p>
 * minStack.min();   --> 返回 -2.
 * <p>  
 * <p>
 * 提示：
 * <p>
 * 各函数的调用总次数不超过 20000 次
 * <p>
 *
 * @author zhaohuiqiang
 * @date 2020/6/29 16:39
 */
public class MinStack {

    private final Stack<Integer> a;
    private final Stack<Integer> b;

    /**
     * initialize your data structure here.
     */
    public MinStack() {
        a = new Stack<>();
        b = new Stack<>();
    }

    public void push(int x) {
        a.push(x);
        if (b.isEmpty() || b.peek() >= x) b.push(x);
    }

    public void pop() {
        if (a.pop().equals(b.peek())) b.pop();
    }

    public int top() {
        return a.peek();
    }

    public int min() {
        return b.peek();
    }

    /**
     * Your MinStack object will be instantiated and called as such:
     * MinStack obj = new MinStack();
     * obj.push(x);
     * obj.pop();
     * int param_3 = obj.top();
     * int param_4 = obj.min();
     */
    public static void main(String[] args) {
        MinStack minStack = new MinStack();
        minStack.push(-2);
        minStack.push(0);
        minStack.push(-3);
        System.out.println(minStack.min());
        minStack.pop();
        System.out.println(minStack.top());
        System.out.println(minStack.min());
    }

}

