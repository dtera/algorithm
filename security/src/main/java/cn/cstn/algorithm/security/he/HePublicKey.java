package cn.cstn.algorithm.security.he;

import sun.security.util.DerInputStream;

import java.io.IOException;
import java.math.BigInteger;

public interface HePublicKey extends HeKey {
  void init();

  HeCiphertext encrypt(BigInteger m);

  HeCiphertext encryptWithCachedSpace(BigInteger m);

  boolean supportCached();

  HeEvaluator getEvaluator();

  HeFixedBaseModPowSpace getCachedSpace();

  /**
   * decode to PublicKey
   *
   * @param encoded encoded
   * @return PublicKey
   */
  static HePublicKey decodeToPublicKey(byte[] encoded) {
    try {
      DerInputStream dis = new DerInputStream(encoded);
      HeSchemaType schema = HeSchemaType.valueOf(dis.getUTF8String());
      return schema.decodeToPublicKey(dis);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

}
