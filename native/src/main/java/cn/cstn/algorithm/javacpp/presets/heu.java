package cn.cstn.algorithm.javacpp.presets;

import org.bytedeco.javacpp.annotation.Platform;
import org.bytedeco.javacpp.annotation.Properties;
import org.bytedeco.javacpp.tools.Info;
import org.bytedeco.javacpp.tools.InfoMap;
import org.bytedeco.javacpp.tools.InfoMapper;

@Properties(value = {
  @Platform(
    compiler = "cpp17",
    include = {
      "heu/phe_kit.h",
    },
    link = {"phe_kit_all", "tommath", "omp"},
    define = {"MSGPACK_NO_BOOST", "SPDLOG_FMT_EXTERNAL", "SPDLOG_NO_TLS"}
  )
},
  target = "cn.cstn.algorithm.javacpp.heu",
  global = "cn.cstn.algorithm.javacpp.global.heu")
public class heu implements InfoMapper {

  @Override
  public void map(InfoMap infoMap) {
    infoMap
      .put(
        new Info("PheKit::decryptPair_", "PheKit::empty", "PheKit::ed25519", "PheKit::curve25519",
          "PheKit::secp256k1", "PheKit::secp256r1", "PheKit::secp192r1", "PheKit::fourq", "PheKit::sm2")
          .skip()
      )
      /*.put(
        new Info("PheKit::decryptPairs")
          .javaText("public native void decryptPairs(@Const Ciphertext cts, @Cast(\"size_t\") long size, @Cast(\"double**\") @ByPtrPtr double[][] out);")
      )*/
      .put(
        new Info("PheKit")
          .pointerTypes("PheKit")
          .base("cn.cstn.algorithm.javacpp.heu.AbstractPheKit")
      );
  }

}
