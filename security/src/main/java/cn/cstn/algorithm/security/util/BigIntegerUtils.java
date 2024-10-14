package cn.cstn.algorithm.security.util;

import java.math.BigInteger;
import java.security.SecureRandom;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

@SuppressWarnings("unused")
public final class BigIntegerUtils {

  public static int[] toIntArray(BigInteger m) {
    byte[] bs = m.toByteArray();
    int[] res = new int[(bs.length - 1) / 4 + 1];
    for (int i = 0; i < res.length; i++) {
      int offset = 4 * i;
      res[i] = bs[offset];
      if (bs.length > offset + 1) {
        res[i] |= (bs[offset + 1] << 8);
      }
      if (bs.length > offset + 2) {
        res[i] |= (bs[offset + 2] << 16);
      }
      if (bs.length > offset + 3) {
        res[i] |= (bs[offset + 3] << 24);
      }
    }
    return res;
    //return m.mag;
  }

  /**
   * L function: L(x) = (x - 1) / n
   *
   * @param x x
   * @param n n
   * @return L(x) = (x - 1) / n
   */
  public static BigInteger L(BigInteger x, BigInteger n) {
    return x.subtract(ONE).divide(n);
  }

  /**
   * lcm: least common multiple of x and y
   *
   * @param x x
   * @param y y
   * @return lcm of x and y
   */
  public static BigInteger lcm(BigInteger x, BigInteger y) {
    BigInteger gcd = x.gcd(y);
    return (gcd.equals(ONE) ? x : x.divide(gcd)).multiply(y);
  }

  /**
   * Generate an exact bitSize random number with the highest bit being 1.
   *
   * @param bitSize bitSize
   * @param random  random
   * @return an exact bitSize random number with the highest bit being 1
   */
  public static BigInteger randomMonicExactBits(int bitSize, SecureRandom random) {
    BigInteger r;
    do {
      r = new BigInteger(bitSize, random);
    } while (r.bitLength() != bitSize);
    return r;
  }

  /**
   * Generate a random r in [0, n).
   *
   * @param n      n
   * @param random random
   * @return a random r in [0, n)
   */
  public static BigInteger randomLtN(BigInteger n, SecureRandom random) {
    BigInteger r;
    do {
      r = new BigInteger(n.bitLength(), random);
    } while (r.compareTo(ZERO) <= 0 || r.compareTo(n) >= 0);
    return r;
  }

  /**
   * Generate a random r in [0, n) and gcd(r, n) = 1.
   *
   * @param n      n
   * @param random random
   * @return a random r in [0, n) and gcd(r, n) = 1
   */
  public static BigInteger randomLtAndCoPrimeN(BigInteger n, SecureRandom random) {
    return randomLtNAndCoPrimeP(n, n, random);
  }

  /**
   * Generate a random r in [0, n) and gcd(r, p) = 1.
   *
   * @param n      n
   * @param p      p
   * @param random random
   * @return a random r in [0, n) and gcd(r, p) = 1
   */
  public static BigInteger randomLtNAndCoPrimeP(BigInteger n, BigInteger p, SecureRandom random) {
    BigInteger r;
    do {
      r = randomLtN(n, random);
    } while (!r.gcd(p).equals(BigInteger.ONE));
    return r;
  }

}
