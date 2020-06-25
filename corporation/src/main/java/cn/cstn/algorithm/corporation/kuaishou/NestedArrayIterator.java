package cn.cstn.algorithm.corporation.kuaishou;

import lombok.RequiredArgsConstructor;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * NestedArrayIterator
 *
 * @author zhaohuiqiang
 * @date 2020/6/25 10:27
 */
@RequiredArgsConstructor
public class NestedArrayIterator {
    private final Object[] arr;
    private int outerCursor = -1;
    private int innerCursor = -1;

    public boolean hasNext() {
        if (outerCursor >= arr.length) return false;

        if (outerCursor == -1 || !arr[outerCursor].getClass().isArray() ||
                ++innerCursor >= Array.getLength(arr[outerCursor])) {
            do {
                if (++outerCursor >= arr.length) return false;
            } while (arr[outerCursor].getClass().isArray() && Array.getLength(arr[outerCursor]) == 0);
            innerCursor = 0;
        }
        return true;
    }

    public Object next() {
        if (outerCursor >= arr.length) throw new NoSuchElementException("doesn't exists element");
        if (arr[outerCursor].getClass().isArray())
            return Array.get(arr[outerCursor], innerCursor);

        return arr[outerCursor];
    }

    public static void main(String[] args) {
        Object[] arr = {1, 2, new int[]{3, 4, 5, 6}, 7, 8, 9, new int[]{}, new int[]{}, 10,
                new int[]{11, 12}, new int[]{}, new int[]{13}, new int[]{}};
        NestedArrayIterator iterator = new NestedArrayIterator(arr);

        List<Object> res = new ArrayList<>();
        while (iterator.hasNext())
            res.add(iterator.next());

        System.out.println(res);
    }

}
