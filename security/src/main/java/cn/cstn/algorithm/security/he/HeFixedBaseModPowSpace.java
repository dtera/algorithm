package cn.cstn.algorithm.security.he;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.math.BigInteger;

@SuppressWarnings("unused")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class HeFixedBaseModPowSpace {
  private final BigInteger base;
  private final BigInteger modulus;
  private final int expUnitBits;
  private final int expMaxBits;

  private int expUnitMask;
  private int totalStairs;
  private long memSize = 0;
  private BigInteger[] stairs;

  public static HeFixedBaseModPowSpace getInstance(BigInteger base, BigInteger modulus, int density, int expMaxBits) {
    HeFixedBaseModPowSpace space = new HeFixedBaseModPowSpace(base, modulus, density, expMaxBits);
    space.makeCachedTable();
    return space;
  }

  public static HeFixedBaseModPowSpace getInstance(BigInteger base, BigInteger modulus, int density) {
    return getInstance(base, modulus, density, 64);
  }

  public static HeFixedBaseModPowSpace getInstance(BigInteger base, BigInteger modulus) {
    return getInstance(base, modulus, 10);
  }

  /**
   * About stair storage format: <br>
   * Assuming expUnitBits = 3, then out_table stores: <br>
   * g^1, g^2, g^3, g^4, g^5, g^6, g^7 <br>
   * g^8，g^16, g^24, g^32, g^40, g^48, g^56, <br>
   * g^64，g^128, g^192, ... <br>
   * g^512, ... <br>
   * ... <br>
   * Each group (line) has 2^expUnitBits - 1, <br>
   * flattened into a one-dimensional array for storage
   */
  public void makeCachedTable() {
    int stairSize = 1 << expUnitBits;
    expUnitMask = stairSize - 1;
    totalStairs = (expMaxBits + expUnitBits - 1) / expUnitBits;
    stairs = new BigInteger[expUnitMask * totalStairs];

    saveStairCache(base, 0);
    BigInteger preStairBase = base;
    for (int i = 1; i < totalStairs; i++) {
      int offset = expUnitMask * i;
      BigInteger stairBase = stairs[offset - 1].multiply(preStairBase).mod(modulus);
      saveStairCache(stairBase, offset);
      preStairBase = stairBase;
    }
  }

  private void saveStairCache(BigInteger stairBase, int offset) {
    stairs[offset] = stairBase;
    memSize += stairs[offset].bitLength();
    for (int i = 1; i < expUnitMask; i++) {
      stairs[offset + i] = stairBase.multiply(stairs[offset + i - 1]).mod(modulus);
      memSize += stairs[offset + i].bitLength();
    }
  }

  public BigInteger modPow(BigInteger m) {
    return modPow1(m);
  }

  private BigInteger modPow2(BigInteger m) {
    BigInteger res = BigInteger.ONE;
    int[] dp = new int[]{m.intValue()};
    int offset = 0, expUnit;
    for (int d : dp) {
      while (d != 0) {
        expUnit = d & expUnitMask;
        if (expUnit > 0) {
          res = res.multiply(stairs[offset + expUnit - 1]).mod(modulus);
        }
        offset += expUnitMask;
        d >>= expUnitBits;
      }
    }
    return res;
  }

  private BigInteger modPow1(BigInteger m) {
    BigInteger mask_ = BigInteger.valueOf(expUnitMask);
    int j = m.and(mask_).intValue();
    BigInteger res = j == 0 ? BigInteger.ONE : stairs[j - 1];
    int len = m.bitLength() / expUnitBits + (m.bitLength() % expUnitBits == 0 ? 0 : 1);
    for (int i = 1; i < len; i++) {
      m = m.shiftRight(expUnitBits);
      j = m.and(mask_).intValue();
      if (j != 0) {
        res = res.multiply(stairs[i * expUnitMask + j - 1]).mod(modulus);
      }
    }
    return res;
  }

  @Override
  public String toString() {
    long mem = memSize / 8;
    String menUseInfo = mem + "B";
    if (mem >= (1 << 30)) {
      menUseInfo = mem / (1 << 30) + "G";
    } else if (mem >= (1 << 20)) {
      menUseInfo = mem / (1 << 20) + "M";
    } else if (mem >= (1 << 10)) {
      menUseInfo = mem / (1 << 10) + "K";
    }
    return "HeFixedBaseModPowSpace {" +
           "\nbase = " + base +
           "\nmodulus = " + modulus +
           "\nexpUnitBits = " + expUnitBits +
           "\nexpMaxBits = " + expMaxBits +
           "\nexpUnitSize = " + expUnitMask +
           "\ntotalStairLevel = " + totalStairs +
           "\nstairSize = " + stairs.length +
           "\nmemSize = " + menUseInfo +
           "\n}";
  }
}
