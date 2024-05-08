package cn.cstn.algorithm.security.he.ou;

import cn.cstn.algorithm.security.he.HeAbstractKeyPairGenerator;
import cn.cstn.algorithm.security.he.HeKeyPair;
import cn.cstn.algorithm.security.he.HePrivateKey;
import cn.cstn.algorithm.security.he.HePublicKey;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

import static cn.cstn.algorithm.security.util.BigIntegerUtils.*;
import static java.math.BigInteger.ONE;
import static java.math.BigInteger.valueOf;

@NoArgsConstructor
public class OuKeyPairGenerator extends HeAbstractKeyPairGenerator {
  public static final int kPrimeFactorSize1024 = 160;
  public static final int kPrimeFactorSize2048 = 224;
  public static final int kPrimeFactorSize3072 = 256;

  private int primeSize;
  private int primeFactorSize;

  public OuKeyPairGenerator(int keySize, int version) {
    super(keySize, version);
    primeSize = (keySize + 2) / 3;
    if (version == 1) {
      primeFactorSize = kPrimeFactorSize1024;
      if (keySize >= 3072) {
        primeFactorSize = kPrimeFactorSize3072;
      } else if (keySize >= 2048) {
        primeFactorSize = kPrimeFactorSize2048;
      }
    }
  }

  @Override
  public HeKeyPair generateKeyPair() {
    if (version == 1) {
      return generateKeyPairV1();
    }
    return generateKeyPairV0();
  }

  public HeKeyPair generateKeyPairV1() {
    BigInteger p, t, pSquared, gpInv; // private key
    BigInteger n, G, H; // public key
    BigInteger pMinusOne, q, g, gp, g_, u;

    do {
      t = BigInteger.probablePrime(primeFactorSize, random);
      u = randomMonicExactBits(primeSize - primeFactorSize + 2, random);
      p = t.multiply(u).add(ONE);
    } while (!p.isProbablePrime(1));
    pSquared = p.multiply(p);
    q = BigInteger.probablePrime(primeSize + 1, random);
    n = pSquared.multiply(q);
    pMinusOne = p.subtract(ONE);

    do {
      g = randomLtNAndCoPrimeP(n, p, random);
      gp = g.mod(pSquared).modPow(pMinusOne, pSquared);
    } while (gp.equals(BigInteger.ONE));
    G = g.modPow(u, n);
    gpInv = L(gp, p).modInverse(p);

    g_ = randomLtNAndCoPrimeP(n, p, random);
    H = g_.modPow(n.multiply(u), n);

    HePublicKey pk = new OuPublicKey(n, G, H);
    HePrivateKey sk = new OuPrivateKey(p, p.divide(valueOf(2)), t, pSquared, gpInv);

    return new HeKeyPair(pk, sk);
  }

  public HeKeyPair generateKeyPairV0() {
    BigInteger p, pMinusOne, pSquared, gpInv; // private key
    BigInteger n, g, h; // public key
    BigInteger q, gp, g_;

    do {
      p = BigInteger.probablePrime(primeSize, random);
      q = BigInteger.probablePrime(keySize - 2 * primeSize, random);
      pSquared = p.multiply(p);
      n = pSquared.multiply(q);
    } while (n.bitLength() < keySize);
    pMinusOne = p.subtract(ONE);

    do {
      g = randomLtNAndCoPrimeP(n, p, random);
      gp = g.mod(pSquared).modPow(pMinusOne, pSquared);
    } while (gp.equals(BigInteger.ONE));
    gpInv = L(gp, p).modInverse(p);

    g_ = randomLtNAndCoPrimeP(n, p, random);
    h = g_.modPow(n, n);

    HePublicKey pk = new OuPublicKey(n, g, h);
    HePrivateKey sk = new OuPrivateKey(p, p.divide(valueOf(2)), pMinusOne, pSquared, gpInv);

    return new HeKeyPair(pk, sk);
  }

}
