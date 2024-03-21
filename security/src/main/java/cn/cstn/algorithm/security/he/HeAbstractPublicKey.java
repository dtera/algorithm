package cn.cstn.algorithm.security.he;

import java.math.BigInteger;
import java.security.SecureRandom;


public abstract class HeAbstractPublicKey implements HePublicKey {
  protected final SecureRandom random = new SecureRandom();
  protected HeFixedBaseModPowSpace space;

  @Override
  public void init() {
    space = supportCached() ? HeFixedBaseModPowSpace
      .getInstance(getBase(), getModulus(), getDensity(), getExpMaxBits()) : null;
  }

  @Override
  public HeCiphertext encryptWithCachedSpace(BigInteger m) {
    throw new UnsupportedOperationException("encrypt with cached space is not supported");
  }

  @Override
  public boolean supportCached() {
    return getBase() != null;
  }

  @Override
  public HeEvaluator getEvaluator() {
    return new HeEvaluator(this);
  }

  protected BigInteger getBase() {
    return null;
  }

  protected int getDensity() {
    return 8;
  }

  protected int getExpMaxBits() {
    return 64;
  }

}
