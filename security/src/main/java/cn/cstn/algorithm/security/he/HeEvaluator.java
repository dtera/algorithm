package cn.cstn.algorithm.security.he;

import java.math.BigInteger;

@SuppressWarnings("unused")
public class HeEvaluator {
  protected HePublicKey publicKey;

  public HeEvaluator(HePublicKey publicKey) {
    this.publicKey = publicKey;
  }

  public HeCiphertext add(HeCiphertext c1, HeCiphertext c2) {
    return HeCiphertext.valueOf(add(c1.c, c2.c));
  }

  public HeCiphertext sub(HeCiphertext c1, HeCiphertext c2) {
    return add(c1, negate(c2));
  }

  public HeCiphertext mulPlaintext(HeCiphertext c, BigInteger m) {
    return HeCiphertext.valueOf(mulPlaintext(c.c, m));
  }

  public HeCiphertext negate(HeCiphertext c) {
    return HeCiphertext.valueOf(negate(c.c));
  }

  // unwrap
  protected BigInteger add(BigInteger c1, BigInteger c2) {
    return c1.multiply(c2).mod(publicKey.getModulus());
  }

  protected BigInteger sub(BigInteger c1, BigInteger c2) {
    return add(c1, negate(c2));
  }

  protected BigInteger mulPlaintext(BigInteger c, BigInteger m) {
    return c.modPow(m, publicKey.getModulus());
  }

  protected BigInteger negate(BigInteger c) {
    return c.modInverse(publicKey.getModulus());
  }

}
