package cn.cstn.algorithm.security.he;

import lombok.SneakyThrows;
import sun.security.util.DerOutputStream;

import java.math.BigInteger;
import java.security.Key;

public interface HeKey extends Key {

  BigInteger getModulus();

  void encode(DerOutputStream dos);

  @SneakyThrows
  @Override
  default byte[] getEncoded() {
    DerOutputStream dos = new DerOutputStream();
    dos.putUTF8String(getAlgorithm());
    encode(dos);
    dos.flush();
    dos.close();
    return dos.toByteArray();
  }

}
