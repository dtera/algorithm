package cn.cstn.algorithm.security.he.paillier;

import cn.cstn.algorithm.security.he.HeKeyPair;
import cn.cstn.algorithm.security.he.HeKeyPairGenerator;
import cn.cstn.algorithm.security.he.HePrivateKey;
import cn.cstn.algorithm.security.he.HePublicKey;

import java.math.BigInteger;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.valueOf;

public class PaillierKeyPairGenerator extends HeKeyPairGenerator {
  private static final int kPQDifferenceBitLenSub = 2;

  public PaillierKeyPairGenerator(int keysize) {
    super(keysize);
  }

  @Override
  public HeKeyPair generateKeyPair() {
    BigInteger p, q, lambda, mu; // private key
    BigInteger n, nSquared, g; // public key
    BigInteger pMinusOne, qMinusOne, gcd;

    int primeSize = keySize / 2;
    do {
      p = BigInteger.probablePrime(primeSize, random);
      pMinusOne = p.subtract(BigInteger.ONE);
      do {
        q = BigInteger.probablePrime(primeSize, random);
        qMinusOne = q.subtract(BigInteger.ONE);
        gcd = pMinusOne.gcd(qMinusOne);
      } while (!gcd.equals(valueOf(2)) || p.subtract(q).bitLength() < primeSize - kPQDifferenceBitLenSub);
      n = p.multiply(q);
    } while (n.bitLength() < keySize);

    lambda = pMinusOne.multiply(qMinusOne).divide(valueOf(2));
    mu = lambda.modInverse(n);
    nSquared = n.multiply(n);
    g = n.add(ONE);
    HePublicKey pk = new PaillierPublicKey(n, nSquared, g);
    HePrivateKey sk = new PaillierPrivateKey(p, q, n, nSquared, lambda, mu);

    return new HeKeyPair(pk, sk);
  }

}
