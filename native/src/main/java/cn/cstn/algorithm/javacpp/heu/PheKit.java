package cn.cstn.algorithm.javacpp.heu;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.annotation.ByRef;
import org.bytedeco.javacpp.annotation.Const;
import org.bytedeco.javacpp.annotation.Platform;

@SuppressWarnings("unused")
@Platform(
  value = {"linux", "macosx", "windows"},
  compiler = "cpp17",
  include = {
    "heu/phe_kit.h",
  },
  link = {"phe_kit_all", "tommath"},
  define = {"MSGPACK_NO_BOOST", "SPDLOG_FMT_EXTERNAL", "SPDLOG_NO_TLS"}
)
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