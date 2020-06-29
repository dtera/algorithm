package cn.cstn.algorithm.offer;

import java.util.Stack;

/**
 * 输入两个整数序列，第一个序列表示栈的压入顺序，请判断第二个序列是否为该栈的弹出顺序。假设压入栈的所有数字均不相等。
 * 例如，序列 {1,2,3,4,5} 是某栈的压栈序列，序列 {4,5,3,2,1} 是该压栈序列对应的一个弹出序列，
 * 但 {4,3,5,1,2} 就不可能是该压栈序列的弹出序列。
 * <p>
 * <p>
 * 示例 1：
 * <p>
 * 输入：pushed = [1,2,3,4,5], popped = [4,5,3,2,1]
 * <p>
 * 输出：true
 * <p>
 * 解释：我们可以按以下顺序执行：
 * <p>
 * push(1), push(2), push(3), push(4), pop() -> 4,
 * <p>
 * push(5), pop() -> 5, pop() -> 3, pop() -> 2, pop() -> 1
 * <p>
 * <p>
 * 示例 2：
 * <p>
 * 输入：pushed = [1,2,3,4,5], popped = [4,3,5,1,2]
 * <p>
 * 输出：false
 * <p>
 * 解释：1 不能在 2 之前弹出。
 * <p>  
 * <p>
 * 提示：
 * <p>
 * 0 <= pushed.length == popped.length <= 1000
 * <p>
 * 0 <= pushed[i], popped[i] < 1000
 * <p>
 * pushed 是 popped 的排列。
 * <p>
 *
 * @author zhaohuiqiang
 * @date 2020/6/29 16:56
 */
public class StackSequences {

    public static void main(String[] args) {
        int[] pushed = {1, 2, 3, 4, 5}, popped = {4, 5, 3, 2, 1};
        System.out.println(validateStackSequences(pushed, popped));
    }

    public static boolean validateStackSequences(int[] pushed, int[] popped) {
        if (pushed == null || popped == null ) return false;
        if (pushed.length == 0 && popped.length == 0) return true;

        Stack<Integer> stack = new Stack<>();
        int i = 0;
        for (int value : pushed) {
            stack.push(value);
            while (!stack.isEmpty() && i < popped.length && stack.peek() == popped[i]) {
                stack.pop();
                i++;
            }
        }

        return stack.isEmpty();
    }

}
