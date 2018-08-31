package cn.cstn.algorithm.commons;

/**
 * description :        search algorithm
 * @author :            zhaohq
 * date :               2018/4/12 0012 21:40
 */
public class Search {

    public static <T extends Comparable<T>> int rotatedArrFind(T[] arr, T target) {
        int low = 0;
        int high = arr.length - 1;

        while (low <= high) {
            final int mid = (low + high) / 2;
            if (target.compareTo(arr[mid]) == 0) return mid;

            //method1:
            if (arr[mid].compareTo(arr[low]) > 0)
                if (target.compareTo(arr[low]) >= 0 && target.compareTo(arr[mid]) < 0)
                    return BS(arr, target, low, mid - 1);
                else
                    low = mid + 1;
            else if (arr[mid].compareTo(arr[low]) < 0) {
                if (target.compareTo(arr[mid]) > 0 && target.compareTo(arr[high]) <= 0)
                    return BS(arr, target, mid + 1, high);
                else
                    high = mid - 1;
            } else low++;
            
            //method2:
            /*else if (target < arr[mid]) {
                if (arr[mid] > arr[low])
                    if (target >= arr[low])
                        return BS(arr, target, low, mid - 1);
                    else
                        low = mid + 1;
                else high = mid - 1;
            } else {
                if (arr[mid] < arr[high])
                    if (target <= arr[high])
                        return BS(arr, target, mid + 1, high);
                    else
                        high = mid - 1;
                else low = mid + 1;
            }*/

        }

        return -1;
    }

    public static <T extends Comparable<T>> int BS(T[] arr, T target) {
        return BS(arr, target, 0, arr.length - 1);
    }

    public static <T extends Comparable<T>> int BS(T[] arr, T target, int low, int high) {
        int mid = (low + high) / 2;
        while (low <= high) {
            if (arr[mid].compareTo(target) == 0) return mid;
            else if (target.compareTo(arr[mid]) < 0)
                high = mid - 1;
            else
                low = mid + 1;
            mid = (low + high) / 2;
        }
        return -1;
    }
}
