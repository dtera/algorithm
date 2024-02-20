package cn.cstn.algorithm.security.he;

import java.math.BigInteger;

public class HeEvaluator {
  protected HePublicKey publicKey;

  public HeEvaluator(HePublicKey publicKey) {
    this.publicKey = publicKey;
  }

  public Ciphertext add(Ciphertext c1, Ciphertext c2) {
    return Ciphertext.valueOf(add(c1.c, c2.c));
  }

  public Ciphertext sub(Ciphertext c1, Ciphertext c2) {
    return add(c1, negate(c2));
  }

  public Ciphertext mulPlaintext(Ciphertext c, BigInteger m) {
    return Ciphertext.valueOf(mulPlaintext(c.c, m));
  }

  public Ciphertext negate(Ciphertext c) {
    return Ciphertext.valueOf(negate(c.c));
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
