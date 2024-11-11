package cn.cstn.algorithm.javacpp.heu;

import cn.cstn.algorithm.javacpp.preset.heu;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.annotation.Namespace;
import org.bytedeco.javacpp.annotation.Properties;

@SuppressWarnings("unused")
@Properties(inherit = heu.class)
@Namespace("heu::lib::phe")
public class Ciphertext extends Pointer {
  static {
    Loader.load();
  }

  public Ciphertext() {
    allocate();
  }

  private native void allocate();

}