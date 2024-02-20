package cn.cstn.algorithm.security.he;

import lombok.SneakyThrows;
import sun.security.util.DerInputStream;

import java.math.BigInteger;

import static java.math.BigInteger.ONE;

public interface HePrivateKey extends HeKey {
  BigInteger decrypt(Ciphertext c);

  /**
   * L function: L(x) = (x - 1) / n
   *
   * @param x x
   * @param n n
   * @return L(x) = (x - 1) / n
   */
  static BigInteger L(BigInteger x, BigInteger n) {
    return x.subtract(ONE).divide(n);
  }

  /**
   * lcm: least common multiple of x and y
   *
   * @param x x
   * @param y y
   * @return lcm of x and y
   */
  static BigInteger lcm(BigInteger x, BigInteger y) {
    return x.divide(x.gcd(y)).multiply(y);
  }


  /**
   * decode to PrivateKey
   *
   * @param encoded encoded
   * @return PrivateKey
   */
  @SneakyThrows
  static HePrivateKey decodeToPrivateKey(byte[] encoded) {
    if (encoded == null) {
      return null;
    }
    DerInputStream dis = new DerInputStream(encoded);
    HeSchemaType schema = HeSchemaType.valueOf(dis.getUTF8String());
    return schema.decodeToPrivateKey(dis);
  }

}
