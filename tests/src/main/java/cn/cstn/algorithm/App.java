package cn.cstn.algorithm;


import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class App {
  public static void main(String[] args) {
    List<String> list = IntStream.range(1, 101).boxed().map(Object::toString).collect(Collectors.toList());
    List<String> res = IntStream.range(0, list.size())
      .boxed().filter(i -> i % 7 != 0).map(list::get).collect(Collectors.toList());
    System.out.println(list);
    System.out.println(res);

    String str = addStrings("356", "922");
    System.out.println(str);

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
