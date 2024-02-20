package cn.cstn.algorithm.security.he.paillier;

import cn.cstn.algorithm.security.he.Ciphertext;
import cn.cstn.algorithm.security.he.HeEvaluator;
import cn.cstn.algorithm.security.he.HePublicKey;
import cn.cstn.algorithm.security.he.HeSchemaType;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;

import java.math.BigInteger;
import java.util.Random;


@AllArgsConstructor
public class PaillierPublicKey implements HePublicKey {
  private BigInteger n;
  private BigInteger nSquared;
  private BigInteger g;

  @Override
  public Ciphertext encrypt(BigInteger m) {
    BigInteger r;
    do {
      r = new BigInteger(n.bitLength(), new Random());
    } while (r.compareTo(n) >= 0 || !r.gcd(n).equals(BigInteger.ONE));
    return Ciphertext.valueOf(g.modPow(m, getModulus()).multiply(r.modPow(n, getModulus())).mod(getModulus()));
  }

  @Override
  public BigInteger getModulus() {
    return nSquared;
  }

  @Override
  public HeEvaluator getEvaluator() {
    return new HeEvaluator(this);
  }

  @SneakyThrows
  @Override
  public void encode(DerOutputStream dos) {
    dos.putInteger(n);
    dos.putInteger(nSquared);
    dos.putInteger(g);
  }

  @Override
  public String getAlgorithm() {
    return HeSchemaType.PAILLIER.name();
  }

  @Override
  public String getFormat() {
    return "raw";
  }

  @Override
  public String toString() {
    return "PaillierPublicKey{" +
           "\nn=" + n +
           "\nnSquared=" + nSquared +
           "\ng=" + g +
           "\n}";
  }

  @SneakyThrows
  public static HePublicKey decodeToPublicKey(DerInputStream dis) {
    BigInteger n = dis.getBigInteger();
    BigInteger nSquared = dis.getBigInteger();
    BigInteger g = dis.getBigInteger();
    return new PaillierPublicKey(n, nSquared, g);
  }
}
