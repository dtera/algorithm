package cn.cstn.algorithm.offer;

import java.util.PriorityQueue;
import java.util.Queue;

/**
 * 如何得到一个数据流中的中位数？如果从数据流中读出奇数个数值，那么中位数就是所有数值排序之后位于中间的数值。如果从数据流中读出偶数个数值，
 * 那么中位数就是所有数值排序之后中间两个数的平均值。
 * <p>
 * <p>
 * 例如，
 * <p>
 * [2,3,4] 的中位数是 3
 * <p>
 * [2,3] 的中位数是 (2 + 3) / 2 = 2.5
 * <p>
 * 设计一个支持以下两种操作的数据结构：
 * <p>
 * void addNum(int num) - 从数据流中添加一个整数到数据结构中。
 * <p>
 * double findMedian() - 返回目前所有元素的中位数。
 * <p>
 * <p>
 * 示例 1：
 * <p>
 * 输入：
 * <p>
 * ["MedianFinder","addNum","addNum","findMedian","addNum","findMedian"]
 * <p>
 * [[],[1],[2],[],[3],[]]
 * <p>
 * 输出：[null,null,null,1.50000,null,2.00000]
 * <p>
 * <p>
 * 示例 2：
 * <p>
 * 输入：
 * <p>
 * ["MedianFinder","addNum","findMedian","addNum","findMedian"]
 * <p>
 * [[],[2],[],[3],[]]
 * <p>
 * 输出：[null,null,2.00000,null,2.50000]
 * <p>  
 * <p>
 * 限制：
 * <p>
 * 最多会对 addNum、findMedia进行 50000 次调用。
 * <p>
 *
 * @author zhaohuiqiang
 * @date 2020/7/10 8:51
 */
public class MedianFinder {

    /**
     * Your MedianFinder object will be instantiated and called as such:
     * <p>
     * MedianFinder obj = new MedianFinder();
     * <p>
     * obj.addNum(num);
     * <p>
     * double param_2 = obj.findMedian();
     */
    public static void main(String[] args) {
        MedianFinder obj = new MedianFinder();
        obj.addNum(8);
        obj.addNum(2);
        obj.addNum(7);
        obj.addNum(3);
        obj.addNum(4);
        obj.addNum(6);
        obj.addNum(1);
        obj.addNum(5);
        System.out.println(obj.findMedian());
    }

    private final Queue<Integer> a = new PriorityQueue<>((x, y) -> y - x);
    private final Queue<Integer> b = new PriorityQueue<>();

    public void addNum(int num) {
        if (a.size() == b.size()) {
            b.add(num);
            a.add(b.poll());
        } else {
            a.add(num);
            b.add(a.poll());
        }
    }

    public double findMedian() {
        assert a.peek() != null;
        return a.size() > b.size() ? a.peek() : (a.peek() + b.peek()) / 2.0;
    }

}
