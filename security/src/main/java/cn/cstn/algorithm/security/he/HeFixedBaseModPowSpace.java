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

  public void makeCachedTable() {
    int stairSize = 1 << expUnitBits;
    maskSize = stairSize - 1;
    totalStairLevel = expMaxBits / expUnitBits + (expMaxBits % expUnitBits == 0 ? 0 : 1);
    stairs = new BigInteger[maskSize * (totalStairLevel - 1) +
                            (expMaxBits % expUnitBits == 0 ? maskSize : ((1 << (expMaxBits % expUnitBits)) - 1))];

    for (int i = 0; i < (totalStairLevel > 1 ? maskSize : stairs.length); i++) {
      stairs[i] = base.modPow(BigInteger.valueOf(i + 1), modulus);
      memSize += stairs[i].bitLength();
    }
    for (int i = 1; i < totalStairLevel - 1; i++) {
      int offset = maskSize * i;
      int offsetBits = expUnitBits * i;
      for (int j = 0; j < maskSize; j++) {
        stairs[offset + j] = base.modPow(BigInteger.valueOf((j + 1L) << offsetBits), modulus);
        memSize += stairs[offset + j].bitLength();
      }
    }
    int offset = maskSize * (totalStairLevel - 1);
    int offsetBits = expUnitBits * (totalStairLevel - 1);
    for (int j = 0; j < stairs.length - offset; j++) {
      stairs[offset + j] = base.modPow(BigInteger.valueOf((j + 1L) << offsetBits), modulus);
      memSize += stairs[offset + j].bitLength();
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
        res = res.multiply(stairs[i * maskSize + j - 1]);
      }
    }
    return res.mod(modulus);
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
