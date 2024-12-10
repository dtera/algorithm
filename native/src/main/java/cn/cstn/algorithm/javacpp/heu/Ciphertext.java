package cn.cstn.algorithm.javacpp.heu;

import cn.cstn.algorithm.javacpp.presets.heu;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.annotation.Namespace;
import org.bytedeco.javacpp.annotation.Platform;
import org.bytedeco.javacpp.annotation.Properties;

import static cn.cstn.algorithm.javacpp.global.heu.deleteCiphertext;
import static cn.cstn.algorithm.javacpp.global.heu.deleteCiphertexts;

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

  @SuppressWarnings("removal")
  @Override
  protected void finalize() {
    if (capacity > 1) {
      deleteCiphertexts(this);
    } else {
      deleteCiphertext(this);
    }
    close();
  }
}