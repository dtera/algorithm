package cn.cstn.algorithm.commons.math;

/**
 * description :        2 dimension point
 * @author :           zhaohq
 * date :               2018/8/28 0028 21:49
 */
public class Point {
    private int x = 0;
    private int y = 0;

    public Point() {
    }

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
