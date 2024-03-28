package cn.cstn.algorithm.security.he;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.math.BigInteger;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class HeFixedBaseModPowSpace {
  private final BigInteger base;
  private final BigInteger modulus;
  private final int expUnitBits;
  private final int expMaxBits;

  private int maskSize;
  private int totalStairLevel;
  private int memSize = 0;
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
   * About stair storage format:
   * Assuming expUnitBits = 3, then out_table stores:
   * g^1, g^2, g^3, g^4, g^5, g^6, g^7
   * g^8，g^16, g^24, g^32, g^40, g^48, g^56,
   * g^64，g^128, g^192, ...
   * g^512, ...
   * ...
   * Each group (line) has 2^expUnitBits - 1, flattened into a one-dimensional array for storage
   */
  public void makeCachedTable() {
    int stairSize = 1 << expUnitBits;
    maskSize = stairSize - 1;
    totalStairLevel = expMaxBits / expUnitBits + (expMaxBits % expUnitBits == 0 ? 0 : 1);
    stairs = new BigInteger[maskSize * (totalStairLevel - 1) +
                            (expMaxBits % expUnitBits == 0 ? maskSize : ((1 << (expMaxBits % expUnitBits)) - 1))];

    saveLevelCache(base, 0, totalStairLevel > 1 ? maskSize : stairs.length);
    for (int i = 1; i < totalStairLevel - 1; i++) {
      int offset = maskSize * i;
      BigInteger levelBase = stairs[offset - 1].multiply(base).mod(modulus);
      saveLevelCache(levelBase, offset, maskSize);
    }
    if (totalStairLevel > 1) {
      int offset = maskSize * (totalStairLevel - 1);
      BigInteger levelBase = stairs[offset - 1].multiply(base).mod(modulus);
      saveLevelCache(levelBase, offset, stairs.length - offset);
    }
  }

  private void saveLevelCache(BigInteger levelBase, int offset, int levelSize) {
    stairs[offset] = levelBase;
    memSize += stairs[offset].bitLength();
    for (int i = 1; i < levelSize; i++) {
      stairs[offset + i] = levelBase.multiply(stairs[offset + i - 1]).mod(modulus);
      memSize += stairs[offset + i].bitLength();
    }
  }

  public BigInteger modPow(BigInteger m) {
    BigInteger mask = BigInteger.valueOf(maskSize);
    int j = m.and(mask).intValue();
    BigInteger res = j == 0 ? BigInteger.ONE : stairs[j - 1];
    int len = m.bitLength() / expUnitBits + (m.bitLength() % expUnitBits == 0 ? 0 : 1);
    for (int i = 1; i < len; i++) {
      m = m.shiftRight(expUnitBits);
      j = m.and(mask).intValue();
      if (j != 0) {
        res = res.multiply(stairs[i * maskSize + j - 1]).mod(modulus);
      }
    }
    return res;
  }

  @Override
  public String toString() {
    int mem = memSize / 8;
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
           "\nexpUnitSize = " + maskSize +
           "\ntotalStairLevel = " + totalStairLevel +
           "\nstairSize = " + stairs.length +
           "\nmemSize = " + menUseInfo +
           "\n}";
  }
}
