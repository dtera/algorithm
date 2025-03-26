/*
Copyright 2019 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
=======================================================================
*/


package cn.cstn.algorithm.javacpp.heu;

import cn.cstn.algorithm.javacpp.global.heu;
import lombok.SneakyThrows;
import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.annotation.Properties;

import java.io.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@Properties(inherit = cn.cstn.algorithm.javacpp.presets.heu.class)
public abstract class AbstractPheKit extends Pointer {
  private final PheKit pheKit;

  public AbstractPheKit(Pointer p) {
    super(p);
    pheKit = (PheKit) this;
  }

  public byte[] getPubKey() {
    return pheKit.pubKey().getStringBytes();
  }

  public byte[] getSecretKey() {
    return pheKit.secretKey().getStringBytes();
  }

  public Ciphertext encrypts(double[] ms, String mark) {
    Ciphertext ct = pheKit.encrypts(ms, ms.length, mark);
    ct.capacity(ms.length);
    return ct;
  }

  public Ciphertext encrypts(double[] ms) {
    return encrypts(ms, "encrypts");
  }

  public double[] decrypts(Ciphertext cts, String mark) {
    double[] res = new double[(int) cts.capacity()];
    pheKit.decrypts(cts, cts.capacity(), res, mark);
    return res;
  }

  public double[] decrypts(Ciphertext cts) {
    return decrypts(cts, "decrypts");
  }

  public Ciphertext encryptPairs(double[] ms1, double[] ms2, boolean unpack, String mark) {
    assert ms1.length == ms2.length;
    Ciphertext ct = pheKit.encryptPairs(ms1, ms2, ms1.length, unpack, mark);
    ct.capacity(ms1.length * (unpack ? 2L : 1));
    return ct;
  }

  public Ciphertext encryptPairs(double[] ms1, double[] ms2, boolean unpack) {
    return encryptPairs(ms1, ms2, unpack, "encryptPairs");
  }

  public Ciphertext encryptPairs(double[] ms1, double[] ms2, String mark) {
    return encryptPairs(ms1, ms2, false, mark);
  }

  public Ciphertext encryptPairs(double[] ms1, double[] ms2) {
    return encryptPairs(ms1, ms2, "encryptPairs");
  }

  public double[] decryptPair(Ciphertext ct, boolean unpack) {
    double[] res = new double[2];
    pheKit.decryptPair(ct, res, unpack);
    return res;
  }

  public double[] decryptPair(Ciphertext ct) {
    return decryptPair(ct, false);
  }

  public double[] decryptPairs(Ciphertext cts, boolean unpack, String mark) {
    double[] res = new double[(int) cts.capacity() * (unpack ? 1 : 2)];
    pheKit.decryptPairs(cts, res.length / 2, res, unpack, mark);
    return res;
  }

  public double[] decryptPairs(Ciphertext cts, boolean unpack) {
    return decryptPairs(cts, unpack, "decryptPairs");
  }

  public double[] decryptPairs(Ciphertext cts) {
    return decryptPairs(cts, false);
  }

  public Ciphertext adds(Ciphertext cts1, Ciphertext cts2) {
    assert cts1.capacity() == cts2.capacity();
    Ciphertext ct = pheKit.adds(cts1, cts2, cts1.capacity());
    ct.capacity(cts1.capacity());
    return ct;
  }

  public void addInplaces(Ciphertext cts1, Ciphertext cts2) {
    assert cts1.capacity() == cts2.capacity();
    pheKit.addInplaces(cts1, cts2, cts1.capacity());
  }

  public Ciphertext subs(Ciphertext cts1, Ciphertext cts2) {
    assert cts1.capacity() == cts2.capacity();
    Ciphertext ct = pheKit.subs(cts1, cts2, cts1.capacity());
    ct.capacity(cts1.capacity());
    return ct;
  }

  public void subInplaces(Ciphertext cts1, Ciphertext cts2) {
    assert cts1.capacity() == cts2.capacity();
    pheKit.subInplaces(cts1, cts2, cts1.capacity());
  }

  public Ciphertext muls(Ciphertext cts1, double[] pts2) {
    assert cts1.capacity() == pts2.length;
    Ciphertext ct = pheKit.muls(cts1, pts2, pts2.length);
    ct.capacity(cts1.capacity());
    return ct;
  }

  public void mulInplaces(Ciphertext cts1, double[] pts2) {
    assert cts1.capacity() == pts2.length;
    pheKit.mulInplaces(cts1, pts2, pts2.length);
  }

  public Ciphertext negates(Ciphertext cts1) {
    Ciphertext ct = pheKit.negates(cts1, cts1.capacity());
    ct.capacity(cts1.capacity());
    return ct;
  }

  public void negateInplaces(Ciphertext cts1) {
    pheKit.negateInplaces(cts1, cts1.capacity());
  }

  public DoublePointer histogram(double[] grads, int[][] indexes, int num_features) {
    int num_bins = indexes.length / num_features;
    int[] indexSize = Arrays.stream(indexes).mapToInt(v -> v.length).toArray();
    DoublePointer res = pheKit.histogram(new DoublePointer(grads), new PointerPointer<IntPointer>(indexes),
      new IntPointer(indexSize), num_bins, num_features, new BytePointer("histogram"));
    res.capacity(indexes.length);
    return res;
  }

  public Ciphertext histogram(Ciphertext grads, int[][] indexes, int num_features) {
    int num_bins = indexes.length / num_features;
    int[] indexSize = Arrays.stream(indexes).mapToInt(v -> v.length).toArray();
    return pheKit.histogram(grads, new PointerPointer<IntPointer>(indexes), new IntPointer(indexSize),
      num_bins, num_features, new BytePointer("cipher_histogram"));
  }

  public void prettyPrint(TimeUnit tm) {
    pheKit.prettyPrint((byte) tm.ordinal());
  }

  @SuppressWarnings("removal")
  @Override
  protected void finalize() {
    heu.deletePheKit((PheKit) this);
    close();
  }

  public static byte[] cipher2Bytes(Ciphertext ciphertext) {
    return heu.cipher2Bytes(ciphertext).getStringBytes();
  }

  public static Ciphertext bytes2Cipher(byte[] bs) {
    return heu.bytes2Cipher(new BytePointer(bs));
  }

  public static byte[][] ciphers2Bytes(Ciphertext ciphertext) {
    byte[][] res = new byte[(int) ciphertext.capacity()][];
    HeBuffer buffer = heu.ciphers2Bytes(ciphertext, ciphertext.capacity());
    for (int i = 0; i < res.length; i++) {
      res[i] = buffer.get(i).getStringBytes();
    }
    buffer.close();
    return res;
  }

  public static Ciphertext bytes2Ciphers(byte[][] bts) {
    HeBuffer buffer = new HeBuffer(bts.length);
    for (int i = 0; i < bts.length; i++) {
      buffer.set(i, new BytePointer(bts[i]));
    }
    Ciphertext res = heu.bytes2Ciphers(buffer, bts.length);
    res.capacity(bts.length);
    return res;
  }

  public static String getSchemeDirName(SchemaType schemaType, CurveName curveName) {
    return schemaType.name() + (curveName == CurveName.empty ? "" : ("_" + curveName.name()));
  }

  @SneakyThrows
  private static void saveKey(byte[] keyBuffer, String dirPath, boolean isPub, boolean overwrite) {
    File dir = new File(dirPath);
    if (!dir.exists()) {
      if (!dir.mkdirs()) {
        throw new RuntimeException(String.format("create dir %s failed", dirPath));
      }
    }
    File fPath = new File(dir, "id_phe" + (isPub ? ".pub" : ""));
    if (!fPath.exists() || overwrite) {
      try (BufferedWriter bw = new BufferedWriter(new FileWriter(fPath))) {
        bw.write(Base64.getEncoder().encodeToString(keyBuffer));
        bw.flush();
        System.out.printf("save %s key to %s%n", isPub ? "public" : "secret", fPath.getAbsolutePath());
      }
    }
  }

  @SneakyThrows
  private static byte[] readKey(String dirPath, boolean isPub) {
    try (BufferedReader br = new BufferedReader(new FileReader(
      new File(dirPath, "id_phe" + (isPub ? ".pub" : ""))
    ))) {
      return Base64.getDecoder().decode(br.readLine());
    }
  }

  public static void savePubKey(byte[] keyBuffer, String dirPath) {
    savePubKey(keyBuffer, dirPath, false);
  }

  public static void savePubKey(byte[] keyBuffer, String dirPath, boolean overwrite) {
    saveKey(keyBuffer, dirPath, true, overwrite);
  }

  public static byte[] readPubKey(String dirPath) {
    return readKey(dirPath, true);
  }

  public static void saveSecretKey(byte[] keyBuffer, String dirPath) {
    saveSecretKey(keyBuffer, dirPath, false);
  }

  public static void saveSecretKey(byte[] keyBuffer, String dirPath, boolean overwrite) {
    saveKey(keyBuffer, dirPath, false, overwrite);
  }

  public static byte[] readSecretKey(String dirPath) {
    return readKey(dirPath, false);
  }

  public static PheKit newInstance(SchemaType schemaType) {
    return new PheKit(schemaType);
  }

  public static PheKit newInstance(SchemaType schemaType, CurveName curveName) {
    return new PheKit(schemaType, curveName.name());
  }

  public static PheKit newInstance(SchemaType schemaType, long key_size) {
    return newInstance(schemaType, key_size, 10 ^ 6, 10);
  }

  public static PheKit newInstance(SchemaType schemaType, long key_size, long scale, int scaleCnt) {
    return new PheKit(schemaType, key_size, scale, scaleCnt, CurveName.empty.name(), false);
  }

  public static PheKit newInstance(String pkDirPath) {
    return newInstance(pkDirPath, false);
  }

  public static PheKit newInstance(byte[] pkBuffer) {
    return new PheKit(new BytePointer(pkBuffer));
  }

  public static PheKit newInstance(BytePointer pkBuffer, long scale, int scaleCnt) {
    return new PheKit(pkBuffer, scale, scaleCnt, false);
  }

  public static PheKit newInstance(String kDirPath, boolean hasSecret) {
    return hasSecret ? newInstance(readPubKey(kDirPath), readSecretKey(kDirPath)) : newInstance(readPubKey(kDirPath));
  }

  public static PheKit newInstance(byte[] pkBuffer, byte[] skBuffer) {
    return new PheKit(new BytePointer(pkBuffer), new BytePointer(skBuffer));
  }

  public static PheKit newInstance(BytePointer pkBuffer, BytePointer skBuffer, long scale, int scaleCnt) {
    return new PheKit(pkBuffer, skBuffer, scale, scaleCnt, false);
  }

}