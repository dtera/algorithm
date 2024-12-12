/*
Copyright 2019 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
=======================================================================
*/


package cn.cstn.algorithm.javacpp.heu;

import cn.cstn.algorithm.javacpp.presets.heu;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.annotation.Properties;

import static cn.cstn.algorithm.javacpp.global.heu.deletePheKit;

@Properties(inherit = heu.class)
public abstract class AbstractPheKit extends Pointer {
  private final PheKit pheKit;

  public AbstractPheKit(Pointer p) {
    super(p);
    pheKit = (PheKit) this;
  }

  public Ciphertext encrypts(double[] ms) {
    Ciphertext ct = pheKit.encrypts(ms, ms.length);
    ct.capacity(ms.length);
    return ct;
  }

  public double[] decrypts(Ciphertext cts) {
    double[] res = new double[(int) cts.capacity()];
    pheKit.decrypts(cts, cts.capacity(), res);
    return res;
  }

  public Ciphertext encryptPairs(double[] ms1, double[] ms2) {
    assert ms1.length == ms2.length;
    Ciphertext ct = pheKit.encryptPairs(ms1, ms2, ms1.length);
    ct.capacity(ms1.length);
    return ct;
  }

  public double[] decryptPair(Ciphertext ct) {
    double[] res = new double[2];
    pheKit.decryptPair(ct, res);
    return res;
  }

  public double[] decryptPairs(Ciphertext cts) {
    double[] res = new double[(int) cts.capacity() * 2];
    pheKit.decryptPairs(cts, cts.capacity(), res);
    return res;
  }

  public Ciphertext adds(Ciphertext cts1, Ciphertext cts2) {
    assert cts1.capacity() == cts2.capacity();
    Ciphertext ct = pheKit.adds(cts1, cts2, cts1.capacity());
    ct.capacity(cts1.capacity());
    return ct;
  }

  public void addInplaces(Ciphertext cts1, Ciphertext cts2) {
    assert cts1.capacity() == cts2.capacity();
    pheKit.addInplaces(cts1, cts2, cts1.capacity());
  }

  public Ciphertext subs(Ciphertext cts1, Ciphertext cts2) {
    assert cts1.capacity() == cts2.capacity();
    Ciphertext ct = pheKit.subs(cts1, cts2, cts1.capacity());
    ct.capacity(cts1.capacity());
    return ct;
  }

  public void subInplaces(Ciphertext cts1, Ciphertext cts2) {
    assert cts1.capacity() == cts2.capacity();
    pheKit.subInplaces(cts1, cts2, cts1.capacity());
  }

  @SuppressWarnings("removal")
  @Override
  protected void finalize() {
    deletePheKit((PheKit) this);
    close();
  }

  public static PheKit newInstance(SchemaType schemaType) {
    return new PheKit(schemaType);
  }

  public static PheKit newInstance(BytePointer pkBuffer) {
    return new PheKit(pkBuffer);
  }

}
