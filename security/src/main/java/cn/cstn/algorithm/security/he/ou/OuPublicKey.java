package cn.cstn.algorithm.security.he.ou;

import cn.cstn.algorithm.security.he.HeAbstractPublicKey;
import cn.cstn.algorithm.security.he.HeCiphertext;
import cn.cstn.algorithm.security.he.HePublicKey;
import cn.cstn.algorithm.security.he.HeSchemaType;
import lombok.RequiredArgsConstructor;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;

import java.io.IOException;
import java.math.BigInteger;

import static cn.cstn.algorithm.security.util.BigIntegerUtils.randomLtN;


@SuppressWarnings("unused")
@RequiredArgsConstructor
public class OuPublicKey extends HeAbstractPublicKey {
  private final BigInteger n;
  private final BigInteger g; // G = g^u mod n
  private final BigInteger h; // h = g_^n mod n, H = g_^(nu) mod n
  //private HeFixedBaseModPowSpace hSpace;

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
    //BigInteger hr = space.modPow(r);
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
    return 128;
  }

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

  public static HePublicKey decodeToPublicKey(DerInputStream dis) {
    try {
      BigInteger n = dis.getBigInteger();
      BigInteger g = dis.getBigInteger();
      BigInteger h = dis.getBigInteger();
      return new OuPublicKey(n, g, h);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
