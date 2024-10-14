package cn.cstn.algorithm.security.he;

import sun.security.util.DerInputStream;

import java.io.IOException;
import java.math.BigInteger;

public interface HePrivateKey extends HeKey {
  BigInteger decrypt(HeCiphertext c);

  /**
   * decode to PrivateKey
   *
   * @param encoded encoded
   * @return PrivateKey
   */
  static HePrivateKey decodeToPrivateKey(byte[] encoded) {
    if (encoded == null) {
      return null;
    }
    try {
      DerInputStream dis = new DerInputStream(encoded);
      HeSchemaType schema = HeSchemaType.valueOf(dis.getUTF8String());
      return schema.decodeToPrivateKey(dis);
    } catch (IOException | IllegalArgumentException e) {
      throw new RuntimeException(e);
    }
  }

}
