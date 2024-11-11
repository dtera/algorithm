package cn.cstn.algorithm.javacpp.heu;

import cn.cstn.algorithm.javacpp.preset.heu;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.annotation.ByRef;
import org.bytedeco.javacpp.annotation.Const;
import org.bytedeco.javacpp.annotation.Properties;

@SuppressWarnings("unused")
@Properties(inherit = heu.class)
public class PheKit extends Pointer {
  static {
    Loader.load();
  }

  public PheKit(SchemaType schemaType, int keySize, int scale) {
    allocate(schemaType.value, keySize, scale);
  }

  public PheKit(SchemaType schemaType, int keySize) {
    this(schemaType, keySize, (int) 1e6);
  }

  public PheKit(SchemaType schemaType) {
    this(schemaType, 2048);
  }

  private native void allocate(int schemaType, int keySize, int scale);

  public native Ciphertext encrypt(double data);

  public native double decrypt(@Const @ByRef Ciphertext ct);

  public native Ciphertext add(@Const @ByRef Ciphertext ct1, @Const @ByRef Ciphertext ct2);

  public native void addInplace(@ByRef Ciphertext ct1, @Const @ByRef Ciphertext ct2);

}