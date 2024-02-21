package cn.cstn.algorithm.security.he.ou;

import cn.cstn.algorithm.security.he.HeKeyPair;
import cn.cstn.algorithm.security.he.HeKeyPairGenerator;
import cn.cstn.algorithm.security.he.HePrivateKey;
import cn.cstn.algorithm.security.he.HePublicKey;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

import static cn.cstn.algorithm.security.he.HePrivateKey.L;
import static java.math.BigInteger.ONE;

@NoArgsConstructor
public class OuKeyPairGenerator extends HeKeyPairGenerator {

  public OuKeyPairGenerator(int keysize) {
    super(keysize);
  }

  @Override
  public HeKeyPair generateKeyPair() {
    BigInteger p, pMinusOne, pSquared, gpInv; // private key
    BigInteger n, g, h; // public key
    BigInteger q, gp, g_;

    int primeSize = (keySize + 2) / 3;
    do {
      p = BigInteger.probablePrime(primeSize, random);
      q = BigInteger.probablePrime(keySize - 2 * primeSize, random);
      pSquared = p.multiply(p);
      n = pSquared.multiply(q);
    } while (n.bitLength() < keySize);
    pMinusOne = p.subtract(ONE);

    do {
      do {
        g = new BigInteger(keySize, random);
      } while (g.compareTo(n) >= 0 || !g.gcd(p).equals(ONE));
      gp = g.mod(pSquared).modPow(pMinusOne, pSquared);
    } while (gp.equals(BigInteger.ONE));
    gpInv = L(gp, p).modInverse(p);

    do {
      g_ = new BigInteger(keySize, random);
    } while (g_.compareTo(n) >= 0 || !g_.gcd(p).equals(ONE));
    h = g_.modPow(n, n);

    HePublicKey pk = new OuPublicKey(n, g, h);
    HePrivateKey sk = new OuPrivateKey(p, pMinusOne, pSquared, gpInv);

    return new HeKeyPair(pk, sk);
  }

}
