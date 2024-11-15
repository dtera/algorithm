package cn.cstn.algorithm.javacpp.heu;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.annotation.Namespace;
import org.bytedeco.javacpp.annotation.Platform;

@SuppressWarnings("unused")
@Platform(
  value = {"linux", "macosx", "windows"},
  compiler = "cpp17",
  include = {
    "heu/library/phe/base/serializable_types.h",
  },
  define = {"MSGPACK_NO_BOOST", "SPDLOG_FMT_EXTERNAL", "SPDLOG_NO_TLS"}
)
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