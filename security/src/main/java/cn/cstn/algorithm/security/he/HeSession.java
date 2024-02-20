package cn.cstn.algorithm.security.he;

import java.math.BigInteger;

public class HeSession {
  private HePublicKey pk;
  private HePrivateKey sk;
  private HeEvaluator evaluator;

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
  }

  public Ciphertext encrypt(BigInteger m) {
    return pk.encrypt(m);
  }

  public BigInteger decrypt(Ciphertext c) {
    if (sk == null) {
      throw new HeException("Decrypt is not supported");
    }
    return sk.decrypt(c);
  }

  public Ciphertext add(Ciphertext c1, Ciphertext c2) {
    return evaluator.add(c1, c2);
  }

  public Ciphertext sub(Ciphertext c1, Ciphertext c2) {
    return evaluator.sub(c1, c2);
  }

  public Ciphertext mulPlaintext(Ciphertext c, BigInteger m) {
    return evaluator.mulPlaintext(c, m);
  }

}
