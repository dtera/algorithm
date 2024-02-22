package cn.cstn.algorithm.security.he.paillier;

import cn.cstn.algorithm.security.he.HeAbstractPublicKey;
import cn.cstn.algorithm.security.he.HeCiphertext;
import cn.cstn.algorithm.security.he.HePublicKey;
import cn.cstn.algorithm.security.he.HeSchemaType;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;

import java.math.BigInteger;
import java.security.SecureRandom;

import static cn.cstn.algorithm.security.he.HeUtils.randomLtAndCoPrimeN;

@AllArgsConstructor
public class PaillierPublicKey extends HeAbstractPublicKey {
  private BigInteger n;
  private BigInteger nSquared;
  // private BigInteger g; // g = n + 1
  private final SecureRandom random = new SecureRandom();

  @Override
  public HeCiphertext encrypt(BigInteger m) {
    BigInteger r = randomLtAndCoPrimeN(n, random);
    // BigInteger gm = g.modPow(m, getModulus());
    BigInteger gm = m.multiply(n).add(BigInteger.ONE);
    return HeCiphertext.valueOf(gm.multiply(r.modPow(n, getModulus())).mod(getModulus()));
  }

  @Override
  public BigInteger getModulus() {
    return nSquared;
  }

  @SneakyThrows
  @Override
  public void encode(DerOutputStream dos) {
    dos.putInteger(n);
    dos.putInteger(nSquared);
    // dos.putInteger(g);
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
           //"\ng=" + g +
           "\n}";
  }

  @SneakyThrows
  public static HePublicKey decodeToPublicKey(DerInputStream dis) {
    BigInteger n = dis.getBigInteger();
    BigInteger nSquared = dis.getBigInteger();
    // BigInteger g = dis.getBigInteger();
    // return new PaillierPublicKey(n, nSquared, g);
    return new PaillierPublicKey(n, nSquared);
  }
}
