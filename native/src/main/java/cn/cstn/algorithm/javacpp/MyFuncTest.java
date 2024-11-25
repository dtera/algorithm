package cn.cstn.algorithm.javacpp;

/**
 * Native Demo
 */

public class MyFuncTest {

  public static void main(String[] args) {
    try (MyFunc myFunc = new MyFunc()) {
      System.out.println(myFunc.add(111, 222));
      System.out.println(myFunc.sub(777, 222));
    }
  }

}
