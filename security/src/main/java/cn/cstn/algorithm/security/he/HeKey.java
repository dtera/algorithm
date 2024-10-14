package cn.cstn.algorithm.security.he;

import sun.security.util.DerOutputStream;

import java.io.IOException;
import java.math.BigInteger;
import java.security.Key;

public interface HeKey extends Key {

  BigInteger getModulus();

  void encode(DerOutputStream dos);

  @Override
  default byte[] getEncoded() {
    try {
      DerOutputStream dos = new DerOutputStream();
      dos.putUTF8String(getAlgorithm());
      encode(dos);
      dos.flush();
      dos.close();
      return dos.toByteArray();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
