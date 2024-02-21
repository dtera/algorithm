package cn.cstn.algorithm.security.he.ou;

import cn.cstn.algorithm.security.he.HeCiphertext;
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
public class OuPublicKey implements HePublicKey {
  private BigInteger n;
  private BigInteger g;
  private BigInteger h; // h = g_^n mod n

  @Override
  public HeCiphertext encrypt(BigInteger m) {
    BigInteger r;
    do {
      r = new BigInteger(n.bitLength(), new Random());
    } while (r.compareTo(n) >= 0);
    BigInteger gm = g.modPow(m, getModulus());
    return HeCiphertext.valueOf(gm.multiply(h.modPow(r, getModulus())).mod(getModulus()));
  }

  @Override
  public BigInteger getModulus() {
    return n;
  }

  @Override
  public HeEvaluator getEvaluator() {
    return new HeEvaluator(this);
  }

  @SneakyThrows
  @Override
  public void encode(DerOutputStream dos) {
    dos.putInteger(n);
    dos.putInteger(g);
    dos.putInteger(h);
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
    return "OuPublicKey{" +
           "\nn=" + n +
           "\ng=" + g +
           "\nh=" + h +
           "\n}";
  }

  @SneakyThrows
  public static HePublicKey decodeToPublicKey(DerInputStream dis) {
    BigInteger n = dis.getBigInteger();
    BigInteger g = dis.getBigInteger();
    BigInteger h = dis.getBigInteger();
    return new OuPublicKey(n, g, h);
  }
}
