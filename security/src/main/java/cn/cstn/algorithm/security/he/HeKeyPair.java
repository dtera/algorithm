package cn.cstn.algorithm.security.he;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class HeKeyPair {
  private final HePublicKey publicKey;
  private final HePrivateKey privateKey;
}
