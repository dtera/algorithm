package cn.cstn.algorithm.security.he;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.math.BigInteger;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Ciphertext {
  public final BigInteger c;

  public static Ciphertext valueOf(BigInteger c) {
    return new Ciphertext(c);
  }

  @Override
  public String toString() {
    return c.toString();
  }
}
