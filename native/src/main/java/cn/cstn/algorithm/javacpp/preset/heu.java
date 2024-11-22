package cn.cstn.algorithm.javacpp.preset;

import org.bytedeco.javacpp.annotation.Platform;
import org.bytedeco.javacpp.annotation.Properties;

@Properties({
  @Platform(
    compiler = "cpp17",
    define = {"MSGPACK_NO_BOOST", "SPDLOG_FMT_EXTERNAL", "SPDLOG_NO_TLS"}
  )
})
public class heu {
}
