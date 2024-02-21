package cn.cstn.algorithm.security.he;

import lombok.RequiredArgsConstructor;

import java.security.SecureRandom;

@RequiredArgsConstructor
public abstract class HeKeyPairGenerator {
  public static final int DEFAULT_KEY_SIZE = 2048;

  protected final int keySize;

  // PRNG to use
  protected SecureRandom random = new SecureRandom();

  public HeKeyPairGenerator() {
    this(DEFAULT_KEY_SIZE);
  }

  public abstract HeKeyPair generateKeyPair();
}
