package cn.cstn.algorithm.javacpp.heu;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SchemaType {
  Mock(0),
  OU(1),
  IPCL(2),
  GPaillier(3),
  ZPaillier(4),
  FPaillier(5),
  IcPaillier(6),
  ClustarFPGA(7),
  ElGamal(8),
  DGK(10),
  DJ(11);

  public final int value;
}
