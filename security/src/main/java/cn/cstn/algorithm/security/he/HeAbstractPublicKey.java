package cn.cstn.algorithm.security.he;

import java.security.SecureRandom;


public abstract class HeAbstractPublicKey implements HePublicKey {
  protected final SecureRandom random = new SecureRandom();

  @Override
  public HeEvaluator getEvaluator() {
    return new HeEvaluator(this);
  }

}
