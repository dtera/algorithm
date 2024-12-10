package cn.cstn.algorithm.javacpp;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.annotation.Platform;

@Platform(compiler = "cpp17", include = "func.h", link = "func")
public class MyFunc extends Pointer {
  static {
    Loader.load();
  }

  public MyFunc() {
    allocate();
  }

  private native void allocate();

  public native int add(int a, int b);

  public native int sub(int a, int b);
}