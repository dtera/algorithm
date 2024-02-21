package cn.cstn.algorithm.security.he;

import org.springframework.util.StopWatch;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.stream.IntStream;

public class HeSession {
  private HePublicKey pk;
  private HePrivateKey sk;
  private HeEvaluator evaluator;
  private StopWatch sw;

  public static HeSession openSession(HeSchemaType schema, int keySize) {
    return new HeSession(schema, keySize);
  }

  public static HeSession openSession(HeSchemaType schema) {
    return new HeSession(schema, HeKeyPairGenerator.DEFAULT_KEY_SIZE);
  }

  public static HeSession openSession(HePublicKey publicKey, HePrivateKey privateKey) {
    return new HeSession(publicKey, privateKey);
  }

  public static HeSession openSession(HePublicKey publicKey) {
    return new HeSession(publicKey);
  }

  public static HeSession openSession(byte[] publicKeyEncoded, byte[] privateKeyEncoded) {
    return new HeSession(publicKeyEncoded, privateKeyEncoded);
  }

  public static HeSession openSession(byte[] publicKeyEncoded) {
    return new HeSession(publicKeyEncoded);
  }

  private HeSession(HeSchemaType schema, int keySize) {
    HeKeyPairGenerator keyGen = schema.keySize(keySize).getKeyPairGenerator();
    HeKeyPair keyPair = keyGen.generateKeyPair();
    this.init(keyPair.getPublicKey(), keyPair.getPrivateKey());
  }

  private HeSession(HePublicKey publicKey, HePrivateKey privateKey) {
    this.init(publicKey, privateKey);
  }

  private HeSession(HePublicKey publicKey) {
    this(publicKey, null);
  }

  private HeSession(byte[] publicKeyEncoded, byte[] privateKeyEncoded) {
    this.init(HePublicKey.decodeToPublicKey(publicKeyEncoded), HePrivateKey.decodeToPrivateKey(privateKeyEncoded));
  }

  private HeSession(byte[] publicKeyEncoded) {
    this(publicKeyEncoded, null);
  }

  private void init(HePublicKey publicKey, HePrivateKey privateKey) {
    this.pk = publicKey;
    this.sk = privateKey;
    this.evaluator = this.pk.getEvaluator();
    this.sw = new StopWatch();
  }

  public HeCiphertext encrypt(BigInteger m) {
    return pk.encrypt(m);
  }

  public HeCiphertext[] encrypt(BigInteger[] ms) {
    sw.start("encrypt");
    HeCiphertext[] res = Arrays.stream(ms).parallel().map(pk::encrypt).toArray(HeCiphertext[]::new);
    sw.stop();
    System.out.printf("%s costs %s ms\n", sw.getLastTaskName(), sw.getLastTaskTimeMillis());
    return res;
  }

  public BigInteger decrypt(HeCiphertext c) {
    if (sk == null) {
      throw new HeException("Decrypt is not supported");
    }
    return sk.decrypt(c);
  }

  public BigInteger[] decrypt(HeCiphertext[] cs) {
    sw.start("decrypt");
    if (sk == null) {
      throw new HeException("Decrypt is not supported");
    }
    BigInteger[] res = Arrays.stream(cs).parallel().map(sk::decrypt).toArray(BigInteger[]::new);
    sw.stop();
    System.out.printf("%s costs %s ms\n", sw.getLastTaskName(), sw.getLastTaskTimeMillis());
    return res;
  }

  public HeCiphertext add(HeCiphertext c1, HeCiphertext c2) {
    return evaluator.add(c1, c2);
  }

  public HeCiphertext[] add(HeCiphertext[] cs1, HeCiphertext[] cs2) {
    sw.start("add");
    HeCiphertext[] res = IntStream.range(0, cs1.length).parallel()
      .mapToObj(i -> evaluator.add(cs1[i], cs2[i])).toArray(HeCiphertext[]::new);
    sw.stop();
    System.out.printf("%s costs %s ms\n", sw.getLastTaskName(), sw.getLastTaskTimeMillis());
    return res;
  }

  public HeCiphertext sub(HeCiphertext c1, HeCiphertext c2) {
    return evaluator.sub(c1, c2);
  }

  public HeCiphertext[] sub(HeCiphertext[] cs1, HeCiphertext[] cs2) {
    sw.start("sub");
    HeCiphertext[] res = IntStream.range(0, cs1.length).parallel()
      .mapToObj(i -> evaluator.sub(cs1[i], cs2[i])).toArray(HeCiphertext[]::new);
    sw.stop();
    System.out.printf("%s costs %s ms\n", sw.getLastTaskName(), sw.getLastTaskTimeMillis());
    return res;
  }

  public HeCiphertext mulPlaintext(HeCiphertext c, BigInteger m) {
    return evaluator.mulPlaintext(c, m);
  }

  public HeCiphertext[] mulPlaintext(HeCiphertext[] cs1, BigInteger[] ms2) {
    sw.start("mulPlaintext");
    HeCiphertext[] res = IntStream.range(0, cs1.length).parallel()
      .mapToObj(i -> evaluator.mulPlaintext(cs1[i], ms2[i])).toArray(HeCiphertext[]::new);
    sw.stop();
    System.out.printf("%s costs %s ms\n", sw.getLastTaskName(), sw.getLastTaskTimeMillis());
    return res;
  }

  @Override
  public String toString() {
    return "HeSession{" +
           "\npk=" + pk +
           "\nsk=" + sk +
           "\n}\n";
  }
}
