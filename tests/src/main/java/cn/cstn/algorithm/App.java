package cn.cstn.algorithm;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SuppressWarnings({"ConstantValue", "unused"})
public class App {

  public static void main(String[] args) {
    // System.out.println(addStrings("356", "922"));
    // stream();
    // strOp();
    // io();
    int[] arr = {7, 21, 11, 15, 1, 16, 8, 19, 2, 4, 6};
    System.out.println(Arrays.toString(arr));
    quickSort(arr);
    System.out.println(Arrays.toString(arr));
  }

  private static void quickSort(int[] arr) {
    quickSort(arr, 0, arr.length - 1);
  }

  private static void quickSort(int[] arr, int l, int r) {
    if (l >= r) return;
    int p = partition(arr, l, r);
    quickSort(arr, l, p - 1);
    quickSort(arr, p + 1, r);
  }

  public static int partition(int[] arr, int l, int r) {
    int i = l - 1, j = l;
    while (j < r) {
      if (arr[j] < arr[r]) {
        i++;
        swap(arr, i, j);
      }
      j++;
    }
    int p = i + 1;
    swap(arr, p, r);

    return p;
  }

  public static void swap(int[] arr, int i, int j) {
    int t = arr[i];
    arr[i] = arr[j];
    arr[j] = t;
  }

  private static void io() throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(App.class.getResourceAsStream("/application.yml")));
    br.lines().forEach(System.out::println);

    char c;
    // 使用 System.in 创建 BufferedReader
    br = new BufferedReader(new InputStreamReader(System.in));
    System.out.println("输入字符, 按下 'q' 键退出。");
    // 读取字符
    do {
      c = (char) br.read();
      System.out.println(c);
    } while (c != 'q');
  }

  private static void strOp() {
    String s = " Hello World  ";
    System.out.println(s.charAt(1)); // 获取索引为1的字符
    System.out.println(s.substring(0, 6)); // 获取索引从0-4的子串“ Hello”
    System.out.println(s.indexOf("o")); // 获取第一个字符o的索引
    System.out.println(s.lastIndexOf("o")); // 获取最后一个字符o的索引
    System.out.println(s.toLowerCase()); // 将字符串变为小写
    System.out.println(s.toUpperCase()); // 将字符串变为大写
    System.out.println(s.concat(", Hi Hx")); // 连接两个字符串
    System.out.println(s.contains("or")); // 判断字符串是否包含“or”
    System.out.println(s.startsWith(" Hello")); // 判断字符串是否以" Hello"开始
    System.out.println(s.endsWith("World  ")); // 判断字符串是否以"World  "开始
    System.out.println(s.equalsIgnoreCase(" hello world  ")); // 忽略大小写，判断字符串是否是否相同
    System.out.println(s.isEmpty()); // 判断字符串是否为空
    System.out.println(s.replace("o", "O")); // 替换字符串中的所有"o"，和replaceAll功能相同
    System.out.println(s.replaceFirst("o", "O")); // 替换字符串中的第一个"o"
    System.out.println(s.replaceAll("o", "O")); // 替换字符串中的所有"o"，和replace功能相同
    System.out.println(Arrays.toString(s.trim().split(" "))); // 以指定分割符分割字符串
    System.out.println(s.trim()); // 去除前后的空格
    System.out.println(s.matches(".*ello.*")); // 正则匹配，判断字符串是否匹配正则表达式
  }

  private static void stream() {
    List<String> list = IntStream.range(1, 101).boxed().map(Object::toString).collect(Collectors.toList());
    List<String> res = IntStream.range(0, list.size())
      .boxed().filter(i -> i % 7 != 0).map(list::get).collect(Collectors.toList());
    System.out.println(list);
    System.out.println(res);
  }

  public static String addStrings(String s1, String s2) {
    int i = s1.length() - 1, j = s2.length() - 1, count = 0;
    StringBuilder sb = new StringBuilder();
    while (i >= 0 || j >= 0 || count != 0) {
      int x = i >= 0 ? s1.charAt(i) - '0' : 0;
      int y = j >= 0 ? s2.charAt(j) - '0' : 0;
      int res = x + y + count;
      sb.append(res % 10);
      count = res / 10;
      i--;
      j--;
    }
    return sb.reverse().toString();
  }

}
