package cn.cstn.algorithm.security.he.paillier;

import cn.cstn.algorithm.security.he.HeCiphertext;
import cn.cstn.algorithm.security.he.HePrivateKey;
import cn.cstn.algorithm.security.he.HeSchemaType;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;

import java.math.BigInteger;

import static cn.cstn.algorithm.security.util.BigIntegerUtils.L;

@RequiredArgsConstructor
public class PaillierPrivateKey implements HePrivateKey {
  private final BigInteger p;
  private final BigInteger q;
  private final BigInteger n;
  private final BigInteger nHalf;
  private final BigInteger nSquared;
  private final BigInteger lambda;
  private final BigInteger mu;

  @Override
  public BigInteger decrypt(HeCiphertext c) {
    BigInteger m = L(c.c.modPow(lambda, getModulus()), n).multiply(mu).mod(n);
    if (m.compareTo(nHalf) > 0) {
      m = m.subtract(n);
    }
    return m;
  }

  @Override
  public BigInteger getModulus() {
    return nSquared;
  }

  @SneakyThrows
  @Override
  public void encode(DerOutputStream dos) {
    dos.putInteger(p);
    dos.putInteger(q);
    dos.putInteger(n);
    dos.putInteger(nHalf);
    dos.putInteger(nSquared);
    dos.putInteger(lambda);
    dos.putInteger(mu);
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
    return "PaillierPrivateKey{" +
           "\np=" + p +
           "\nq=" + q +
           "\nlambda=" + lambda +
           "\nmu=" + mu +
           "\n}";
  }

  @SneakyThrows
  public static HePrivateKey decodeToPrivateKey(DerInputStream dis) {
    BigInteger p = dis.getBigInteger();
    BigInteger q = dis.getBigInteger();
    BigInteger n = dis.getBigInteger();
    BigInteger nHalf = dis.getBigInteger();
    BigInteger nSquared = dis.getBigInteger();
    BigInteger lambda = dis.getBigInteger();
    BigInteger mu = dis.getBigInteger();
    return new PaillierPrivateKey(p, q, n, nHalf, nSquared, lambda, mu);
  }
}
