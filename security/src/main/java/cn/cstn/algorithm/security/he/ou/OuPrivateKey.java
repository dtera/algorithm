package cn.cstn.algorithm.security.he.ou;

import cn.cstn.algorithm.security.he.HeCiphertext;
import cn.cstn.algorithm.security.he.HePrivateKey;
import cn.cstn.algorithm.security.he.HeSchemaType;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;

import java.math.BigInteger;

import static cn.cstn.algorithm.security.he.HePrivateKey.L;
import static java.math.BigInteger.valueOf;

@RequiredArgsConstructor
public class OuPrivateKey implements HePrivateKey {
  private final BigInteger p;
  private final BigInteger pMinusOne;
  private final BigInteger pSquared;
  private final BigInteger gpInv;

  @Override
  public BigInteger decrypt(HeCiphertext c) {
    BigInteger m = L(c.c.modPow(pMinusOne, getModulus()), p).multiply(gpInv).mod(p);
    if (m.compareTo(p.divide(valueOf(2))) > 0) {
      m = m.subtract(p);
    }
    return m;
  }

  @Override
  public BigInteger getModulus() {
    return pSquared;
  }

  @SneakyThrows
  @Override
  public void encode(DerOutputStream dos) {
    dos.putInteger(p);
    dos.putInteger(pMinusOne);
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
           "\np-1=" + pMinusOne +
           "\np^2=" + pSquared +
           "\ngp-1=" + gpInv +
           "\n}";
  }

  @SneakyThrows
  public static HePrivateKey decodeToPrivateKey(DerInputStream dis) {
    BigInteger p = dis.getBigInteger();
    BigInteger pMinusOne = dis.getBigInteger();
    BigInteger pSquared = dis.getBigInteger();
    BigInteger gpInv = dis.getBigInteger();
    return new OuPrivateKey(p, pMinusOne, pSquared, gpInv);
  }
}
