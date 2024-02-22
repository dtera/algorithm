package cn.cstn.algorithm.security.he;

import lombok.SneakyThrows;
import sun.security.util.DerInputStream;

import java.math.BigInteger;

public interface HePrivateKey extends HeKey {
  BigInteger decrypt(HeCiphertext c);

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
