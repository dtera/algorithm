package cn.cstn.algorithm.javacpp.heu;

import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.annotation.Properties;

import static cn.cstn.algorithm.javacpp.global.heu.deleteCiphertext;
import static cn.cstn.algorithm.javacpp.global.heu.deleteCiphertexts;

@Properties(inherit = cn.cstn.algorithm.javacpp.presets.heu.class)
public abstract class AbstractCiphertext extends Pointer {

  public AbstractCiphertext(Pointer p) {
    super(p);
  }

  @SuppressWarnings("removal")
  @Override
  protected void finalize() {
    if (capacity > 1) {
      deleteCiphertexts((Ciphertext) this);
    } else {
      deleteCiphertext((Ciphertext) this);
    }
    close();
  }
}