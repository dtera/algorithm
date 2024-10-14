package cn.cstn.algorithm.security.he.ou;

import cn.cstn.algorithm.security.he.HeCiphertext;
import cn.cstn.algorithm.security.he.HePrivateKey;
import cn.cstn.algorithm.security.he.HeSchemaType;
import lombok.RequiredArgsConstructor;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;

import java.io.IOException;
import java.math.BigInteger;

import static cn.cstn.algorithm.security.util.BigIntegerUtils.L;

//VM options: --add-exports java.base/sun.security.util=ALL-UNNAMED
@SuppressWarnings("unused")
@RequiredArgsConstructor
public class OuPrivateKey implements HePrivateKey {
  private final BigInteger p;
  private final BigInteger pHalf;
  private final BigInteger t;
  private final BigInteger pSquared;
  private final BigInteger gpInv;

  @Override
  public BigInteger decrypt(HeCiphertext c) {
    BigInteger m = L(c.c.mod(getModulus()).modPow(t, getModulus()), p).multiply(gpInv).mod(p);
    if (m.compareTo(pHalf) > 0) {
      m = m.subtract(p);
    }
    return m;
  }

  @Override
  public BigInteger getModulus() {
    return pSquared;
  }

  @Override
  public void encode(DerOutputStream dos) {
    dos.putInteger(p);
    dos.putInteger(pHalf);
    dos.putInteger(t);
    dos.putInteger(pSquared);
    dos.putInteger(gpInv);
  }

  @Override
  public String getAlgorithm() {
    return HeSchemaType.OU.name();
  }

  @Override
  public String getFormat() {
    return "raw";
  }

  @Override
  public String toString() {
    return "PaillierPrivateKey{" +
           "\np=" + p +
           "\nt=" + t +
           "\ngp-1=" + gpInv +
           "\n}";
  }

  public static HePrivateKey decodeToPrivateKey(DerInputStream dis) {
    try {
      BigInteger p = dis.getBigInteger();
      BigInteger pHalf = dis.getBigInteger();
      BigInteger t = dis.getBigInteger();
      BigInteger pSquared = dis.getBigInteger();
      BigInteger gpInv = dis.getBigInteger();
      return new OuPrivateKey(p, pHalf, t, pSquared, gpInv);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
