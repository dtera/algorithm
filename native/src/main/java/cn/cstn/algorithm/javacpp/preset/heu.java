package cn.cstn.algorithm.javacpp.preset;

import org.bytedeco.javacpp.ClassProperties;
import org.bytedeco.javacpp.LoadEnabled;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.annotation.NoException;
import org.bytedeco.javacpp.annotation.Platform;
import org.bytedeco.javacpp.annotation.Properties;
import org.bytedeco.javacpp.tools.InfoMap;
import org.bytedeco.javacpp.tools.InfoMapper;

import java.util.List;

@Properties(
  value = {
    @Platform(
      value = {"linux", "macosx", "windows"},
      compiler = "cpp17",
      include = {
        "heu/phe_kit.h",
      },
      link = {"phe_kit_all"},
      define = {"MSGPACK_NO_BOOST", "SPDLOG_FMT_EXTERNAL", "SPDLOG_NO_TLS"}
    ),
    @Platform(
      value = {"linux", "macosx", "windows"},
      extension = {"-gpu"})
  })
@NoException
public class heu implements LoadEnabled, InfoMapper {
  @Override
  public void map(InfoMap infoMap) {

  }

  @Override
  public void init(ClassProperties properties) {
    String platform = properties.getProperty("platform");
    String extension = properties.getProperty("platform.extension");
    List<String> preloads = properties.get("platform.preload");
    List<String> resources = properties.get("platform.preloadresource");
    List<String> preloadpaths = properties.get("platform.preloadpath");

    String vcredistdir = System.getenv("VCToolsRedistDir");
    if (vcredistdir != null && !vcredistdir.isEmpty()) {
      switch (platform) {
        case "windows-x86":
          preloadpaths.add(0, vcredistdir + "\\x86\\Microsoft.VC142.CRT");
          preloadpaths.add(1, vcredistdir + "\\x86\\Microsoft.VC142.OpenMP");
          preloadpaths.add(2, vcredistdir + "\\x86\\Microsoft.VC141.CRT");
          preloadpaths.add(3, vcredistdir + "\\x86\\Microsoft.VC141.OpenMP");
          break;
        case "windows-x86_64":
          preloadpaths.add(0, vcredistdir + "\\x64\\Microsoft.VC142.CRT");
          preloadpaths.add(1, vcredistdir + "\\x64\\Microsoft.VC142.OpenMP");
          preloadpaths.add(2, vcredistdir + "\\x64\\Microsoft.VC141.CRT");
          preloadpaths.add(3, vcredistdir + "\\x64\\Microsoft.VC141.OpenMP");
          break;
        default:
          // not Windows
      }
    }

    // Only apply this at load time
    if (!Loader.isLoadLibraries()) {
      return;
    }

    // Let users enable loading of the full version of MKL
    String load =
      System.getProperty(
          "org.bytedeco.openblas.load", System.getProperty("org.bytedeco.mklml.load", ""))
        .toLowerCase();

    int i = 0;
    if (load.equals("mkl") || load.equals("mkl_rt")) {
      String[] libs = {
        "iomp5",
        "libiomp5md",
        "mkl_core",
        "mkl_avx",
        "mkl_avx2",
        "mkl_avx512",
        "mkl_avx512_mic",
        "mkl_def",
        "mkl_mc",
        "mkl_mc3",
        "mkl_intel_lp64",
        "mkl_intel_thread",
        "mkl_gnu_thread",
        "mkl_rt"
      };
      for (i = 0; i < libs.length; i++) {
        preloads.add(i, libs[i] + "#" + libs[i]);
      }
      load = "mkl_rt";
      resources.add("/org/bytedeco/mkl/");
    }

    if (!load.isEmpty()) {
      if (platform.startsWith("linux")) {
        preloads.add(i, load + "#mklml_intel");
      } else if (platform.startsWith("macosx")) {
        preloads.add(i, load + "#mklml");
      } else if (platform.startsWith("windows")) {
        preloads.add(i, load + "#mklml");
      }
    }

    // Only apply this at load time since we don't want to copy the CUDA libraries here
    if (!Loader.isLoadLibraries() || extension == null || !extension.endsWith("-gpu")) {
      return;
    }
    String[] libs = {
      "cudart",
      "cublasLt",
      "cublas",
      "cufft",
      "curand",
      "cusolver",
      "cusparse",
      "cudnn",
      "nccl",
      "nvrtc",
      "myelin",
      "nvinfer",
      "cudnn_ops_infer",
      "cudnn_ops_train",
      "cudnn_adv_infer",
      "cudnn_adv_train",
      "cudnn_cnn_infer",
      "cudnn_cnn_train"
    };
    for (String lib : libs) {
      if (platform.startsWith("linux")) {
        lib +=
          lib.startsWith("cudnn")
            ? "@.8"
            : lib.equals("nccl")
            ? "@.2"
            : lib.equals("myelin")
            ? "@.1"
            : lib.equals("nvinfer")
            ? "@.7"
            : lib.equals("cufft") || lib.equals("curand") || lib.equals("cusolver")
            ? "@.10"
            : lib.equals("cudart")
            ? "@.11.0"
            : lib.equals("nvrtc") ? "@.11.0" : "@.11";
      } else if (platform.startsWith("windows")) {
        lib +=
          lib.startsWith("cudnn")
            ? "64_8"
            : lib.equals("nccl")
            ? "64_2"
            : lib.equals("myelin")
            ? "64_1"
            : lib.equals("nvinfer")
            ? "64_7"
            : lib.equals("cufft") || lib.equals("curand") || lib.equals("cusolver")
            ? "64_10"
            : lib.equals("cudart")
            ? "64_110"
            : lib.equals("nvrtc") ? "64_110_0" : "64_11";
      } else {
        continue; // no CUDA
      }
      if (!preloads.contains(lib)) {
        preloads.add(i++, lib);
      }
    }
    if (i > 0) {
      resources.add("/org/bytedeco/cuda/");
      resources.add("/org/bytedeco/tensorrt/");
    }
  }
}
