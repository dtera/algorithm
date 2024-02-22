package cn.cstn.algorithm.security.he.ou;

import cn.cstn.algorithm.security.he.HeAbstractPublicKey;
import cn.cstn.algorithm.security.he.HeCiphertext;
import cn.cstn.algorithm.security.he.HePublicKey;
import cn.cstn.algorithm.security.he.HeSchemaType;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;

import java.math.BigInteger;

import static cn.cstn.algorithm.security.he.HeUtils.randomLtN;


@AllArgsConstructor
public class OuPublicKey extends HeAbstractPublicKey {
  private BigInteger n;
  private BigInteger g; // G = g^u mod n
  private BigInteger h; // h = g_^n mod n, H = g_^(nu) mod n

  @Override
  public HeCiphertext encrypt(BigInteger m) {
    BigInteger r = randomLtN(n, random);
    BigInteger gm = g.modPow(m, getModulus());
    return HeCiphertext.valueOf(gm.multiply(h.modPow(r, getModulus())).mod(getModulus()));
  }

  @Override
  public BigInteger getModulus() {
    return n;
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
