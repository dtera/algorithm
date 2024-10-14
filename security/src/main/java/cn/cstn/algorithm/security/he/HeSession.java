package cn.cstn.algorithm.security.he;

import lombok.Setter;
import org.springframework.util.StopWatch;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.stream.IntStream;

@SuppressWarnings("unused")
public class HeSession {
  private HePublicKey pk;
  private HePrivateKey sk;
  private HeEvaluator evaluator;
  private StopWatch sw;
  @Setter
  private boolean supportCached = true;

  public static HeSession openSession(HeSchemaType schema, int keySize) {
    return openSession(schema, keySize, true);
  }

  public static HeSession openSession(HeSchemaType schema) {
    return openSession(schema, HeAbstractKeyPairGenerator.DEFAULT_KEY_SIZE, true);
  }

  public static HeSession openSession(HePublicKey publicKey, HePrivateKey privateKey) {
    return openSession(publicKey, privateKey, true);
  }

  public static HeSession openSession(HePublicKey publicKey) {
    return openSession(publicKey, true);
  }

  public static HeSession openSession(byte[] publicKeyEncoded, byte[] privateKeyEncoded) {
    return openSession(publicKeyEncoded, privateKeyEncoded, true);
  }

  public static HeSession openSession(HeSchemaType schema, int keySize, boolean supportCached) {
    return new HeSession(schema, keySize, supportCached);
  }

  public static HeSession openSession(HeSchemaType schema, boolean supportCached) {
    return new HeSession(schema, HeAbstractKeyPairGenerator.DEFAULT_KEY_SIZE, supportCached);
  }

  public static HeSession openSession(HePublicKey publicKey, HePrivateKey privateKey, boolean supportCached) {
    return new HeSession(publicKey, privateKey, supportCached);
  }

  public static HeSession openSession(HePublicKey publicKey, boolean supportCached) {
    return new HeSession(publicKey, supportCached);
  }

  public static HeSession openSession(byte[] publicKeyEncoded, byte[] privateKeyEncoded, boolean supportCached) {
    return new HeSession(publicKeyEncoded, privateKeyEncoded, supportCached);
  }

  public static HeSession openSession(byte[] publicKeyEncoded, boolean supportCached) {
    return new HeSession(publicKeyEncoded, supportCached);
  }

  private HeSession(HeSchemaType schema, int keySize, boolean supportCached) {
    HeAbstractKeyPairGenerator keyGen = schema.keySize(keySize).getKeyPairGenerator();
    HeKeyPair keyPair = keyGen.generateKeyPair();
    this.init(keyPair.getPublicKey(), keyPair.getPrivateKey(), supportCached);
  }

  private HeSession(HeSchemaType schema, int keySize) {
    this(schema, keySize, true);
  }

  private HeSession(HePublicKey publicKey, HePrivateKey privateKey, boolean supportCached) {
    this.init(publicKey, privateKey, supportCached);
  }

  private HeSession(HePublicKey publicKey, boolean supportCached) {
    this(publicKey, null, supportCached);
  }

  private HeSession(byte[] publicKeyEncoded, byte[] privateKeyEncoded, boolean supportCached) {
    this.init(HePublicKey.decodeToPublicKey(publicKeyEncoded), HePrivateKey.decodeToPrivateKey(privateKeyEncoded),
      supportCached);
  }

  private HeSession(byte[] publicKeyEncoded, boolean supportCached) {
    this(publicKeyEncoded, null, supportCached);
  }

  private void init(HePublicKey publicKey, HePrivateKey privateKey, boolean supportCached) {
    this.pk = publicKey;
    this.pk.init();
    this.sk = privateKey;
    this.evaluator = this.pk.getEvaluator();
    this.sw = new StopWatch();
    this.supportCached = supportCached;
  }

  public HeCiphertext encrypt(BigInteger m) {
    if (supportCached && pk.supportCached()) {
      return pk.encryptWithCachedSpace(m);
    } else {
      return pk.encrypt(m);
    }
  }

  public HeCiphertext[] encrypt(BigInteger[] ms) {
    sw.start("encrypt");
    HeCiphertext[] res = supportCached && pk.supportCached() ?
      Arrays.stream(ms).parallel().map(pk::encryptWithCachedSpace).toArray(HeCiphertext[]::new) :
      Arrays.stream(ms).parallel().map(pk::encrypt).toArray(HeCiphertext[]::new);
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
    return "HeSession {" +
           "\npk=" + pk +
           "\ncachedSpace=" + pk.getCachedSpace() +
           "\nsk=" + sk +
           "\n}\n";
  }
}
