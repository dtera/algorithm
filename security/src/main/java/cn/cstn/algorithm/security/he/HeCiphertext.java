package cn.cstn.algorithm.security.he;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.math.BigInteger;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class HeCiphertext {
  public final BigInteger c;

  public static HeCiphertext valueOf(BigInteger c) {
    return new HeCiphertext(c);
  }

  @Override
  public String toString() {
    return c.toString();
  }
}
