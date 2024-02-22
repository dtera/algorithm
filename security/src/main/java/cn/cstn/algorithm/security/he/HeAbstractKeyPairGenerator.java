package cn.cstn.algorithm.security.he;

import lombok.RequiredArgsConstructor;

import java.security.SecureRandom;

@RequiredArgsConstructor
public abstract class HeAbstractKeyPairGenerator {
  public static final int DEFAULT_KEY_SIZE = 2048;

  protected final int keySize;
  protected final int version;

  // PRNG to use
  protected SecureRandom random = new SecureRandom();

  public HeAbstractKeyPairGenerator() {
    this(DEFAULT_KEY_SIZE, 0);
  }

  public abstract HeKeyPair generateKeyPair();
}
