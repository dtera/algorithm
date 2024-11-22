package cn.cstn.algorithm.javacpp.heu;

import cn.cstn.algorithm.javacpp.preset.heu;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.annotation.Namespace;
import org.bytedeco.javacpp.annotation.Platform;
import org.bytedeco.javacpp.annotation.Properties;

@Platform(
  include = {
    "heu/library/phe/base/serializable_types.h",
  }
)
@Namespace("heu::lib::phe")
@Properties(inherit = heu.class)
public class Ciphertext extends Pointer {
  static {
    Loader.load();
  }

  public Ciphertext() {
    allocate();
  }

  private native void allocate();

}