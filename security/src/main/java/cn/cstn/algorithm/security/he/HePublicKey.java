package cn.cstn.algorithm.security.he;

import lombok.SneakyThrows;
import sun.security.util.DerInputStream;

import java.math.BigInteger;

public interface HePublicKey extends HeKey {
  void init();

  HeCiphertext encrypt(BigInteger m);

  HeCiphertext encryptWithCachedSpace(BigInteger m);

  boolean supportCached();

  HeEvaluator getEvaluator();

  /**
   * decode to PublicKey
   *
   * @param encoded encoded
   * @return PublicKey
   */
  @SneakyThrows
  static HePublicKey decodeToPublicKey(byte[] encoded) {
    DerInputStream dis = new DerInputStream(encoded);
    HeSchemaType schema = HeSchemaType.valueOf(dis.getUTF8String());
    return schema.decodeToPublicKey(dis);
  }

}
