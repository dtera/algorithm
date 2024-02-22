package cn.cstn.algorithm.security.he.paillier;

import cn.cstn.algorithm.security.he.HeAbstractKeyPairGenerator;
import cn.cstn.algorithm.security.he.HeKeyPair;
import cn.cstn.algorithm.security.he.HePrivateKey;
import cn.cstn.algorithm.security.he.HePublicKey;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.valueOf;

@NoArgsConstructor
public class PaillierKeyPairGenerator extends HeAbstractKeyPairGenerator {
  private static final int kPQDifferenceBitLenSub = 2;

  public PaillierKeyPairGenerator(int keySize, int version) {
    super(keySize, version);
  }

  @Override
  public HeKeyPair generateKeyPair() {
    BigInteger p, q, lambda, mu; // private key
    BigInteger n, nSquared; // public key
    // BigInteger g; // public key
    BigInteger pMinusOne, qMinusOne, gcd;

    int primeSize = keySize / 2;
    do {
      p = BigInteger.probablePrime(primeSize, random);
      pMinusOne = p.subtract(ONE);
      do {
        q = BigInteger.probablePrime(primeSize, random);
        qMinusOne = q.subtract(ONE);
        gcd = pMinusOne.gcd(qMinusOne);
      } while (!gcd.equals(valueOf(2)) || p.subtract(q).bitLength() < primeSize - kPQDifferenceBitLenSub);
      n = p.multiply(q);
    } while (n.bitLength() < keySize);

    lambda = pMinusOne.multiply(qMinusOne).divide(valueOf(2));
    nSquared = n.multiply(n);
    // g = n.add(ONE);
    // mu = L(g.modPow(lambda, nSquared), n).modInverse(n); // mu = L(g^lambda mod n^2)^-1 mod n
    mu = lambda.modInverse(n);
    HePublicKey pk = new PaillierPublicKey(n, nSquared); // new PaillierPublicKey(n, nSquared, g);
    HePrivateKey sk = new PaillierPrivateKey(p, q, n, n.divide(valueOf(2)), nSquared, lambda, mu);

    return new HeKeyPair(pk, sk);
  }

}
