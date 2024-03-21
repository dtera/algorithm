package cn.cstn.algorithm.security.he.ou;

import cn.cstn.algorithm.security.he.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;

import java.math.BigInteger;

import static cn.cstn.algorithm.security.he.HeUtils.randomLtN;


@RequiredArgsConstructor
public class OuPublicKey extends HeAbstractPublicKey {
  private final BigInteger n;
  private final BigInteger g; // G = g^u mod n
  private final BigInteger h; // h = g_^n mod n, H = g_^(nu) mod n
  private HeFixedBaseModPowSpace hSpace;

  @Override
  public void init() {
    super.init();
    // hSpace = HeFixedBaseModPowSpace.getInstance(h, getModulus(), getDensity(), getExpMaxBits());
  }

  @Override
  public HeCiphertext encrypt(BigInteger m) {
    BigInteger r = randomLtN(n, random);
    BigInteger gm = g.modPow(m, getModulus());
    return HeCiphertext.valueOf(gm.multiply(h.modPow(r, getModulus())).mod(getModulus()));
  }

  @Override
  public HeCiphertext encryptWithCachedSpace(BigInteger m) {
    BigInteger r = randomLtN(n, random);
    BigInteger gm = space.modPow(m);
    BigInteger hr = h.modPow(r, getModulus());
    return HeCiphertext.valueOf(gm.multiply(hr).mod(getModulus()));
  }

  @Override
  public BigInteger getBase() {
    return g;
  }

  @Override
  public BigInteger getModulus() {
    return n;
  }

  @Override
  protected int getExpMaxBits() {
    // return n.bitLength();
    return super.getExpMaxBits();
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
    return "OuPublicKey{" + "\nn=" + n + "\ng=" + g + "\nh=" + h + "\n}";
  }

  @SneakyThrows
  public static HePublicKey decodeToPublicKey(DerInputStream dis) {
    BigInteger n = dis.getBigInteger();
    BigInteger g = dis.getBigInteger();
    BigInteger h = dis.getBigInteger();
    return new OuPublicKey(n, g, h);
  }

}
