package cn.cstn.algorithm.javacpp.presets;

import org.bytedeco.javacpp.annotation.Platform;
import org.bytedeco.javacpp.annotation.Properties;
import org.bytedeco.javacpp.tools.Info;
import org.bytedeco.javacpp.tools.InfoMap;
import org.bytedeco.javacpp.tools.InfoMapper;

@Properties(value = {
  @Platform(
    value = {"linux", "macosx", "windows"},
    compiler = "cpp17",
    include = {
      "heu/phe_kit.h",
      "heu/he_types.h"
    },
    link = {"phe_kit_all", "tommath", "FourQ", "crypto", "ssl", "omp"},
    define = {"MSGPACK_NO_BOOST", "SPDLOG_FMT_EXTERNAL", "SPDLOG_NO_TLS"}
  ),
  @Platform(
    value = "linux",
    link = {"phe_kit_all", "tommath", "FourQ", "crypto", "ssl", "omp", "FourQ_group", "openssl"}
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
          "PheKit::secp256k1", "PheKit::secp256r1", "PheKit::secp192r1", "PheKit::fourq", "PheKit::sm2", "genIndexes",
          "Ciphertext::data", "Ciphertext::c_data", "yacl::ByteContainerView")
          .skip()
      )
      .put(
        new Info("PheKit")
          .pointerTypes("PheKit")
          .base("cn.cstn.algorithm.javacpp.heu.AbstractPheKit")
      )
      .put(
        new Info("Ciphertext")
          .pointerTypes("Ciphertext")
          .base("cn.cstn.algorithm.javacpp.heu.AbstractCiphertext")
      );
  }

}
