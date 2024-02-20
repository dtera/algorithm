package cn.cstn.algorithm.security.he;

import java.security.SecureRandom;

public abstract class HeKeyPairGenerator {
  public static final int DEFAULT_KEY_SIZE = 2048;

  protected int keySize;

  // PRNG to use
  protected SecureRandom random;

  public HeKeyPairGenerator() {
    this(DEFAULT_KEY_SIZE);
  }

  public HeKeyPairGenerator(int keysize) {
    initialize(keysize, null);
  }

  public void initialize(int keysize, SecureRandom random) {
    this.keySize = keysize;
    this.random = (random == null ? new SecureRandom() : random);
  }

  public abstract HeKeyPair generateKeyPair();
}
