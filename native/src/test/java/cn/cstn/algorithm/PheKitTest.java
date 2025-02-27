package cn.cstn.algorithm;

import cn.cstn.algorithm.javacpp.heu.Ciphertext;
import cn.cstn.algorithm.javacpp.heu.CurveName;
import cn.cstn.algorithm.javacpp.heu.PheKit;
import cn.cstn.algorithm.javacpp.heu.SchemaType;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.springframework.util.StopWatch;

import java.util.concurrent.TimeUnit;

/**
 * Unit test for PheKit.
 */
public class PheKitTest extends TestCase {
  /**
   * Create the test case
   *
   * @param testName name of the test case
   */
  public PheKitTest(String testName) {
    super(testName);
  }

  /**
   * @return the suite of tests being tested
   */
  @SuppressWarnings({"TextBlockMigration"})
  public static Test suite() {
    return new TestSuite(PheKitTest.class);
  }

  /**
   * PubKey
   */
  public void pubKey(SchemaType schemaType, CurveName curveName) {
    try (PheKit pheKit = PheKit.newInstance(schemaType, curveName);
         PheKit pheKit2 = PheKit.newInstance(pheKit.getPubKey())) {
      Ciphertext ct1 = pheKit2.encrypt(2);
      Ciphertext ct2 = pheKit2.encrypt(3);
      Ciphertext addRes = pheKit2.add(ct1, ct2);
      System.out.printf("add: %f\n", pheKit.decrypt(addRes));
      pheKit2.addInplace(addRes, ct2);
      System.out.printf("addInplace: %f\n", pheKit.decrypt(addRes));
      Ciphertext subRes = pheKit2.sub(ct2, ct1);
      System.out.printf("sub: %f\n", pheKit.decrypt(subRes));
      pheKit2.subInplace(subRes, ct2);
      System.out.printf("subInplace: %f\n", pheKit.decrypt(subRes));
    }
  }

  /**
   * Cipher serial and deserialize
   */
  public void cipherSerialDeserialize(SchemaType schemaType, CurveName curveName) {
    try (PheKit pheKit = PheKit.newInstance(schemaType, curveName)) {
      Ciphertext c1 = pheKit.encrypt(2);
      Ciphertext c2 = pheKit.encrypt(3);
      byte[] bt1 = PheKit.cipher2Bytes(c1);
      byte[] bt2 = PheKit.cipher2Bytes(c2);
      Ciphertext ct1 = PheKit.bytes2Cipher(bt1);
      Ciphertext ct2 = PheKit.bytes2Cipher(bt2);

      Ciphertext addRes = pheKit.add(ct1, ct2);
      System.out.printf("add: %f\n", pheKit.decrypt(addRes));
      pheKit.addInplace(addRes, ct2);
      System.out.printf("addInplace: %f\n", pheKit.decrypt(addRes));
      Ciphertext subRes = pheKit.sub(ct2, ct1);
      System.out.printf("sub: %f\n", pheKit.decrypt(subRes));
      pheKit.subInplace(subRes, ct2);
      System.out.printf("subInplace: %f\n", pheKit.decrypt(subRes));
    }
  }

  /**
   * Ciphers serial and deserialize
   */
  public void ciphersSerialDeserialize(SchemaType schemaType, int size, CurveName curveName) {
    try (PheKit pheKit = PheKit.newInstance(schemaType, curveName)) {
      StopWatch sw = new StopWatch();
      sw.start("init");
      double[] ms1 = new double[size], ms2 = new double[size], res1 = new double[size], res2 = new double[size];
      for (int i = 0; i < size; i++) {
        ms1[i] = i + (double) i / 10;
        ms2[i] = i + (double) (i + 3) / 10;
        res1[i] = ms1[i] + ms2[i];
        res2[i] = res1[i] + ms2[i];
      }
      sw.stop();
      sw.start("[cts1]encrypts");
      Ciphertext cs1 = pheKit.encrypts(ms1, "[cts1]encrypts");
      sw.stop();
      sw.start("[cts2]encrypts");
      Ciphertext cs2 = pheKit.encrypts(ms2, "[cts2]encrypts");
      sw.stop();

      sw.start("serial");
      byte[][] buf1 = PheKit.ciphers2Bytes(cs1);
      byte[][] buf2 = PheKit.ciphers2Bytes(cs2);
      sw.stop();
      sw.start("deserial");
      Ciphertext cts1 = PheKit.bytes2Ciphers(buf1);
      Ciphertext cts2 = PheKit.bytes2Ciphers(buf2);
      sw.stop();

      sw.start("adds");
      Ciphertext addRes = pheKit.adds(cts1, cts2);
      sw.stop();
      sw.start("[add]decrypts");
      double[] res = pheKit.decrypts(addRes, "[add]decrypts");
      sw.stop();
      System.out.printf("[adds]real: [%f, %f, %f, %f]\t", res1[0], res1[1], res1[2], res1[3]);
      System.out.printf("res: [%f, %f, %f, %f]\n", res[0], res[1], res[2], res[3]);
      sw.start("addInplaces");
      pheKit.addInplaces(addRes, cts2);
      sw.stop();
      sw.start("[addInplaces]decrypts");
      res = pheKit.decrypts(addRes, "[addInplaces]decrypts");
      sw.stop();
      System.out.printf("[addInplaces]real: [%f, %f, %f, %f]\t", res2[0], res2[1], res2[2], res2[3]);
      System.out.printf("res: [%f, %f, %f, %f]\n", res[0], res[1], res[2], res[3]);

      sw.start("subs");
      Ciphertext subRes = pheKit.subs(addRes, cts2);
      sw.stop();
      sw.start("[subs]decrypts");
      res = pheKit.decrypts(subRes, "[subs]decrypts");
      sw.stop();
      System.out.printf("[subs]real: [%f, %f, %f, %f]\t", res1[0], res1[1], res1[2], res1[3]);
      System.out.printf("res: [%f, %f, %f, %f]\n", res[0], res[1], res[2], res[3]);
      sw.start("subInplaces");
      pheKit.subInplaces(subRes, cts2);
      sw.stop();
      sw.start("[subInplaces]decrypts");
      res = pheKit.decrypts(subRes, "[subInplaces]decrypts");
      sw.stop();
      System.out.printf("[subInplaces]real: [%f, %f, %f, %f]\t", ms1[0], ms1[1], ms1[2], ms1[3]);
      System.out.printf("res: [%f, %f, %f, %f]\n", res[0], res[1], res[2], res[3]);

      System.out.println(sw.prettyPrint());
      pheKit.prettyPrint(TimeUnit.SECONDS);
    }
  }

  /**
   * Single Op
   */
  public void singleOp(SchemaType schemaType, CurveName curveName) {
    try (PheKit pheKit = PheKit.newInstance(schemaType, curveName)) {
      Ciphertext ct1 = pheKit.encrypt(2);
      Ciphertext ct2 = pheKit.encrypt(3);

      Ciphertext addRes = pheKit.add(ct1, ct2);
      System.out.printf("add: %f\n", pheKit.decrypt(addRes));
      pheKit.addInplace(addRes, ct2);
      System.out.printf("addInplace: %f\n", pheKit.decrypt(addRes));

      Ciphertext subRes = pheKit.sub(ct2, ct1);
      System.out.printf("sub: %f\n", pheKit.decrypt(subRes));
      pheKit.subInplace(subRes, ct2);
      System.out.printf("subInplace: %f\n", pheKit.decrypt(subRes));

      if (schemaType != SchemaType.ElGamal) {
        Ciphertext mulRes = pheKit.mul(ct1, 5);
        System.out.printf("mul: %f\n", pheKit.decrypt(mulRes));
        pheKit.mulInplace(mulRes, 7);
        System.out.printf("mulInplace: %f\n", pheKit.decrypt(mulRes));

        Ciphertext negRes = pheKit.negate(mulRes);
        System.out.printf("negate: %f\n", pheKit.decrypt(negRes));
        pheKit.negateInplace(negRes);
        System.out.printf("negateInplace: %f\n", pheKit.decrypt(negRes));
      }
    }
  }

  /**
   * Single Pair Op
   */
  public void singlePairOp(SchemaType schemaType, CurveName curveName, boolean unpack) {
    try (PheKit pheKit = PheKit.newInstance(schemaType, curveName)) {
      Ciphertext ct1 = pheKit.encryptPair(2.1, 4.3, unpack);
      Ciphertext ct2 = pheKit.encryptPair(3.2, 5.4, unpack);
      Ciphertext addRes = pheKit.add(ct1, ct2, unpack);
      double[] res = pheKit.decryptPair(addRes, unpack);
      System.out.printf("add: [%f, %f]\n", res[0], res[1]);
      pheKit.addInplace(addRes, ct2, unpack);
      res = pheKit.decryptPair(addRes, unpack);
      System.out.printf("addInplace: [%f, %f]\n", res[0], res[1]);
      Ciphertext subRes = pheKit.sub(ct2, ct1, unpack);
      res = pheKit.decryptPair(subRes, unpack);
      System.out.printf("sub: [%f, %f]\n", res[0], res[1]);
      pheKit.subInplace(subRes, ct2, unpack);
      res = pheKit.decryptPair(subRes, unpack);
      System.out.printf("subInplace: [%f, %f]\n", res[0], res[1]);
    }
  }

  /**
   * Batch Op
   */
  public void batchOp(SchemaType schemaType, int size, CurveName curveName) {
    try (PheKit pheKit = PheKit.newInstance(schemaType, curveName)) {
      StopWatch sw = new StopWatch();
      sw.start("init");
      double[] ms1 = new double[size], ms2 = new double[size], res1 = new double[size], res2 = new double[size],
        res3 = new double[size], res4 = new double[size];
      for (int i = 0; i < size; i++) {
        ms1[i] = i + (double) i / 10;
        ms2[i] = i + (double) (i + 3) / 10;
        res1[i] = ms1[i] + ms2[i];
        res2[i] = res1[i] + ms2[i];
        res3[i] = ms1[i] * ms2[i];
        res4[i] = res3[i] * ms2[i];
      }
      sw.stop();
      sw.start("[cts1]encrypts");
      Ciphertext cts1 = pheKit.encrypts(ms1, "[cts1]encrypts");
      sw.stop();
      sw.start("[cts2]encrypts");
      Ciphertext cts2 = pheKit.encrypts(ms2, "[cts2]encrypts");
      sw.stop();

      sw.start("adds");
      Ciphertext addRes = pheKit.adds(cts1, cts2);
      sw.stop();
      sw.start("[add]decrypts");
      double[] res = pheKit.decrypts(addRes, "[add]decrypts");
      sw.stop();
      System.out.printf("[adds]real: [%f, %f, %f, %f]\t", res1[0], res1[1], res1[2], res1[3]);
      System.out.printf("res: [%f, %f, %f, %f]\n", res[0], res[1], res[2], res[3]);
      sw.start("addInplaces");
      pheKit.addInplaces(addRes, cts2);
      sw.stop();
      sw.start("[addInplaces]decrypts");
      res = pheKit.decrypts(addRes, "[addInplaces]decrypts");
      sw.stop();
      System.out.printf("[addInplaces]real: [%f, %f, %f, %f]\t", res2[0], res2[1], res2[2], res2[3]);
      System.out.printf("res: [%f, %f, %f, %f]\n", res[0], res[1], res[2], res[3]);

      sw.start("subs");
      Ciphertext subRes = pheKit.subs(addRes, cts2);
      sw.stop();
      sw.start("[subs]decrypts");
      res = pheKit.decrypts(subRes, "[subs]decrypts");
      sw.stop();
      System.out.printf("[subs]real: [%f, %f, %f, %f]\t", res1[0], res1[1], res1[2], res1[3]);
      System.out.printf("res: [%f, %f, %f, %f]\n", res[0], res[1], res[2], res[3]);
      sw.start("subInplaces");
      pheKit.subInplaces(subRes, cts2);
      sw.stop();
      sw.start("[subInplaces]decrypts");
      res = pheKit.decrypts(subRes, "[subInplaces]decrypts");
      sw.stop();
      System.out.printf("[subInplaces]real: [%f, %f, %f, %f]\t", ms1[0], ms1[1], ms1[2], ms1[3]);
      System.out.printf("res: [%f, %f, %f, %f]\n", res[0], res[1], res[2], res[3]);

      if (schemaType != SchemaType.ElGamal) {
        sw.start("muls");
        Ciphertext mulRes = pheKit.muls(cts1, ms2);
        sw.stop();
        sw.start("[muls]decrypts");
        res = pheKit.decrypts(mulRes, "[muls]decrypts");
        sw.stop();
        System.out.printf("[muls]real: [%f, %f, %f, %f]\t", res3[0], res3[1], res3[2], res3[3]);
        System.out.printf("res: [%f, %f, %f, %f]\n", res[0], res[1], res[2], res[3]);
        sw.start("mulInplaces");
        pheKit.mulInplaces(mulRes, ms2);
        sw.stop();
        sw.start("[mulInplaces]decrypts");
        res = pheKit.decrypts(mulRes, "[mulInplaces]decrypts");
        sw.stop();
        System.out.printf("[mulInplaces]real: [%f, %f, %f, %f]\t", res4[0], res4[1], res4[2], res4[3]);
        System.out.printf("res: [%f, %f, %f, %f]\n", res[0], res[1], res[2], res[3]);

        sw.start("negs");
        Ciphertext negRes = pheKit.negates(mulRes);
        sw.stop();
        sw.start("[negs]decrypts");
        res = pheKit.decrypts(negRes, "[negs]decrypts");
        sw.stop();
        System.out.printf("[negs]real: [%f, %f, %f, %f]\t", -res4[0], -res4[1], -res4[2], -res4[3]);
        System.out.printf("res: [%f, %f, %f, %f]\n", res[0], res[1], res[2], res[3]);
        sw.start("negInplaces");
        pheKit.negateInplaces(negRes);
        sw.stop();
        sw.start("[negInplaces]decrypts");
        res = pheKit.decrypts(negRes, "[negInplaces]decrypts");
        sw.stop();
        System.out.printf("[negInplaces]real: [%f, %f, %f, %f]\t", res4[0], res4[1], res4[2], res4[3]);
        System.out.printf("res: [%f, %f, %f, %f]\n", res[0], res[1], res[2], res[3]);
      }

      System.out.println(sw.prettyPrint());
      pheKit.prettyPrint(TimeUnit.SECONDS);
    }
  }

  /**
   * Batch Pair Op
   */
  public void batchPairOp(SchemaType schemaType, int size, CurveName curveName, boolean unpack) {
    try (PheKit pheKit = PheKit.newInstance(schemaType, curveName)) {
      StopWatch sw = new StopWatch();
      sw.start("init");
      double[] ms11 = new double[size], ms12 = new double[size], res11 = new double[size], res12 = new double[size];
      double[] ms21 = new double[size], ms22 = new double[size], res21 = new double[size], res22 = new double[size];
      for (int i = 0; i < size; i++) {
        ms11[i] = i + (double) i / 10;
        ms12[i] = i + (double) (i + 3) / 10;
        ms21[i] = i + (double) i / 100;
        ms22[i] = i + (double) (i + 3) / 100;
        res11[i] = ms11[i] + ms21[i];
        res12[i] = ms12[i] + ms22[i];
        res21[i] = res11[i] + ms21[i];
        res22[i] = res12[i] + ms22[i];
      }
      sw.stop();
      sw.start("[cts1]encryptPairs");
      Ciphertext cts1 = pheKit.encryptPairs(ms11, ms12, unpack, "[cts1]encryptPairs");
      sw.stop();
      sw.start("[cts2]encryptPairs");
      Ciphertext cts2 = pheKit.encryptPairs(ms21, ms22, unpack, "[cts2]encryptPairs");
      sw.stop();

      sw.start("adds");
      Ciphertext addRes = pheKit.adds(cts1, cts2);
      sw.stop();
      sw.start("[adds]decryptPairs");
      double[] res = pheKit.decryptPairs(addRes, unpack, "[adds]decryptPairs");
      sw.stop();
      System.out.printf("[add]real: [(%f, %f), (%f, %f), (%f, %f), (%f, %f)]\t", res11[0], res12[0], res11[1], res12[1], res11[2], res12[2], res11[3], res12[3]);
      System.out.printf("res: [(%f, %f), (%f, %f), (%f, %f), (%f, %f)]\n", res[0], res[size], res[1], res[size + 1], res[2], res[size + 2], res[3], res[size + 3]);
      sw.start("addInplaces");
      pheKit.addInplaces(addRes, cts2);
      sw.stop();
      sw.start("[addInplaces]decryptPairs");
      res = pheKit.decryptPairs(addRes, unpack, "[addInplaces]decryptPairs");
      sw.stop();
      System.out.printf("[addInplaces]real: [(%f, %f), (%f, %f), (%f, %f), (%f, %f)]\t", res21[0], res22[0], res21[1], res22[1], res21[2], res22[2], res21[3], res22[3]);
      System.out.printf("res: [(%f, %f), (%f, %f), (%f, %f), (%f, %f)]\n", res[0], res[size], res[1], res[size + 1], res[2], res[size + 2], res[3], res[size + 3]);

      sw.start("subs");
      Ciphertext subRes = pheKit.subs(addRes, cts2);
      sw.stop();
      sw.start("[subs]decryptPairs");
      res = pheKit.decryptPairs(subRes, unpack, "[subs]decryptPairs");
      sw.stop();
      System.out.printf("[subs]real: [(%f, %f), (%f, %f), (%f, %f), (%f, %f)]\t", res11[0], res12[0], res11[1], res12[1], res11[2], res12[2], res11[3], res12[3]);
      System.out.printf("res: [(%f, %f), (%f, %f), (%f, %f), (%f, %f)]\n", res[0], res[size], res[1], res[size + 1], res[2], res[size + 2], res[3], res[size + 3]);
      sw.start("subInplaces");
      pheKit.subInplaces(subRes, cts2);
      sw.stop();
      sw.start("[subInplaces]decryptPairs");
      res = pheKit.decryptPairs(subRes, unpack, "[subInplaces]decryptPairs");
      sw.stop();
      System.out.printf("[subInplaces]real: [(%f, %f), (%f, %f), (%f, %f), (%f, %f)]\t", ms11[0], ms12[0], ms11[1], ms12[1], ms11[2], ms12[2], ms11[3], ms12[3]);
      System.out.printf("res: [(%f, %f), (%f, %f), (%f, %f), (%f, %f)]\n", res[0], res[size], res[1], res[size + 1], res[2], res[size + 2], res[3], res[size + 3]);

      System.out.println(sw.prettyPrint());
      pheKit.prettyPrint(TimeUnit.SECONDS);
    }
  }

  /**
   * PubKey Test For OU
   */
  public void testOUPubKey() {
    pubKey(SchemaType.OU, CurveName.empty);
  }

  /**
   * Cipher serial and deserialize Test For OU
   */
  public void testOUCipherSerialDeserial() {
    cipherSerialDeserialize(SchemaType.OU, CurveName.empty);
  }

  /**
   * Ciphers serial and deserialize Test For OU
   */
  public void testOUCiphersSerialDeserial() {
    ciphersSerialDeserialize(SchemaType.OU, 10000, CurveName.empty);
  }

  /**
   * Single Op Test For OU
   */
  public void testOUSingleOp() {
    singleOp(SchemaType.OU, CurveName.empty);
  }

  /**
   * Batch Op Test For OU
   */
  public void testOUBatchOp() {
    batchOp(SchemaType.OU, 100000, CurveName.empty);
  }

  /**
   * Single Pair Op Test For OU
   */
  public void testOUSinglePairOp() {
    singlePairOp(SchemaType.OU, CurveName.empty, false);
  }

  /**
   * Batch Pair Op Test For OU
   */
  public void testOUBatchPairOp() {
    batchPairOp(SchemaType.OU, 100000, CurveName.empty, false);
  }

  /**
   * PubKey Test For ElGamal, curve is Ed25519
   */
  public void testElGamalEd25519PubKey() {
    pubKey(SchemaType.ElGamal, CurveName.ed25519);
  }

  /**
   * SingleOp Test For ElGamal, curve is Ed25519
   */
  public void testElGamalEd25519SingleOp() {
    singleOp(SchemaType.ElGamal, CurveName.ed25519);
  }

  /**
   * BatchOp Test For ElGamal, curve is Ed25519
   */
  public void testElGamalEd25519BatchOp() {
    batchOp(SchemaType.ElGamal, 10000, CurveName.ed25519);
  }

  /**
   * PubKey Test For ElGamal, curve is FourQ
   */
  public void testElGamalFourQPubKey() {
    pubKey(SchemaType.ElGamal, CurveName.fourq);
  }

  /**
   * SingleOp Test For ElGamal, curve is FourQ
   */
  public void testElGamalFourQSingleOp() {
    singleOp(SchemaType.ElGamal, CurveName.fourq);
  }

  /**
   * BatchOp Test For ElGamal, curve is FourQ
   */
  public void testElGamalFourQBatchOp() {
    batchOp(SchemaType.ElGamal, 10000, CurveName.fourq);
  }

  /**
   * PubKey Test For ElGamal, curve is Sm2
   */
  public void testElGamalSm2PubKey() {
    pubKey(SchemaType.ElGamal, CurveName.sm2);
  }

  /**
   * SingleOp Test For ElGamal, curve is Sm2
   */
  public void testElGamalSm2SingleOp() {
    singleOp(SchemaType.ElGamal, CurveName.sm2);
  }

  /**
   * BatchOp Test For ElGamal, curve is Sm2
   */
  public void testElGamalSm2BatchOp() {
    batchOp(SchemaType.ElGamal, 10000, CurveName.sm2);
  }

  /**
   * PubKey Test For ElGamal, curve is Secp256k1
   */
  public void testElGamalSecp256k1PubKey() {
    pubKey(SchemaType.ElGamal, CurveName.secp256k1);
  }

  /**
   * SingleOp Test For ElGamal, curve is Secp256k1
   */
  public void testElGamalSecp256k1SingleOp() {
    singleOp(SchemaType.ElGamal, CurveName.secp256k1);
  }

  /**
   * BatchOp Test For ElGamal, curve is Secp256k1
   */
  public void testElGamalSecp256k1BatchOp() {
    batchOp(SchemaType.ElGamal, 10000, CurveName.secp256k1);
  }

  /**
   * PubKey Test For ElGamal, curve is Secp256r1
   */
  public void testElGamalSecp256r1PubKey() {
    pubKey(SchemaType.ElGamal, CurveName.secp256r1);
  }

  /**
   * SingleOp Test For ElGamal, curve is Secp256r1
   */
  public void testElGamalSecp256r1SingleOp() {
    singleOp(SchemaType.ElGamal, CurveName.secp256r1);
  }

  /**
   * BatchOp Test For ElGamal, curve is Secp256r1
   */
  public void testElGamalSecp256r1BatchOp() {
    batchOp(SchemaType.ElGamal, 10000, CurveName.secp256r1);
  }

  /**
   * PubKey Test For ElGamal, curve is Secp192r1
   */
  public void testElGamalSecp192r1PubKey() {
    pubKey(SchemaType.ElGamal, CurveName.secp192r1);
  }

  /**
   * SingleOp Test For ElGamal, curve is Secp192r1
   */
  public void testElGamalSecp192r1SingleOp() {
    singleOp(SchemaType.ElGamal, CurveName.secp192r1);
  }

  /**
   * BatchOp Test For ElGamal, curve is Secp192r1
   */
  public void testElGamalSecp192r1BatchOp() {
    batchOp(SchemaType.ElGamal, 10000, CurveName.secp192r1);
  }

  /**
   * Single Pair Op Test For ElGamal, curve is Ed25519
   */
  public void testElGamalEd25519SinglePairOp() {
    singlePairOp(SchemaType.ElGamal, CurveName.ed25519, true);
  }

  /**
   * Batch Pair Op Test For ElGamal, curve is Ed25519
   */
  public void testElGamalEd25519BatchPairOp() {
    batchPairOp(SchemaType.ElGamal, 10000, CurveName.ed25519, true);
  }

}
