package cn.cstn.algorithm;

import cn.cstn.algorithm.javacpp.heu.Ciphertext;
import cn.cstn.algorithm.javacpp.heu.CurveName;
import cn.cstn.algorithm.javacpp.heu.PheKit;
import cn.cstn.algorithm.javacpp.heu.SchemaType;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.springframework.util.StopWatch;

/**
 * Unit test for simple App.
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
         PheKit pheKit2 = PheKit.newInstance(pheKit.pubKey())) {
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
    }
  }

  /**
   * Single Pair Op
   */
  public void singlePairOp(SchemaType schemaType, CurveName curveName) {
    try (PheKit pheKit = PheKit.newInstance(schemaType, curveName)) {
      Ciphertext ct1 = pheKit.encryptPair(2.1, 4.3);
      Ciphertext ct2 = pheKit.encryptPair(3.2, 5.4);
      Ciphertext addRes = pheKit.add(ct1, ct2);
      double[] res = pheKit.decryptPair(addRes);
      System.out.printf("add: [%f, %f]\n", res[0], res[1]);
      pheKit.addInplace(addRes, ct2);
      res = pheKit.decryptPair(addRes);
      System.out.printf("addInplace: [%f, %f]\n", res[0], res[1]);
      Ciphertext subRes = pheKit.sub(ct2, ct1);
      res = pheKit.decryptPair(subRes);
      System.out.printf("sub: [%f, %f]\n", res[0], res[1]);
      pheKit.subInplace(subRes, ct2);
      res = pheKit.decryptPair(subRes);
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
      double[] ms1 = new double[size], ms2 = new double[size], res1 = new double[size], res2 = new double[size];
      for (int i = 0; i < size; i++) {
        ms1[i] = i + (double) i / 10;
        ms2[i] = i + (double) (i + 3) / 10;
        res1[i] = ms1[i] + ms2[i];
        res2[i] = res1[i] + ms2[i];
      }
      sw.stop();
      sw.start("encrypt ms1");
      Ciphertext cts1 = pheKit.encrypts(ms1);
      sw.stop();
      sw.start("encrypt ms2");
      Ciphertext cts2 = pheKit.encrypts(ms2);
      sw.stop();

      sw.start("add");
      Ciphertext addRes = pheKit.adds(cts1, cts2);
      sw.stop();
      sw.start("decrypt add");
      double[] res = pheKit.decrypts(addRes);
      sw.stop();
      System.out.printf("[add]real: [%f, %f, %f, %f]\t", res1[0], res1[1], res1[2], res1[3]);
      System.out.printf("res: [%f, %f, %f, %f]\n", res[0], res[1], res[2], res[3]);
      sw.start("addInplace");
      pheKit.addInplaces(addRes, cts2);
      sw.stop();
      sw.start("decrypt addInplace");
      res = pheKit.decrypts(addRes);
      sw.stop();
      System.out.printf("[addInplace]real: [%f, %f, %f, %f]\t", res2[0], res2[1], res2[2], res2[3]);
      System.out.printf("res: [%f, %f, %f, %f]\n", res[0], res[1], res[2], res[3]);

      sw.start("sub");
      Ciphertext subRes = pheKit.subs(addRes, cts2);
      sw.stop();
      sw.start("decrypt sub");
      res = pheKit.decrypts(subRes);
      sw.stop();
      System.out.printf("[sub]real: [%f, %f, %f, %f]\t", res1[0], res1[1], res1[2], res1[3]);
      System.out.printf("res: [%f, %f, %f, %f]\n", res[0], res[1], res[2], res[3]);
      sw.start("subInplace");
      pheKit.subInplaces(subRes, cts2);
      sw.stop();
      sw.start("decrypt subInplace");
      res = pheKit.decrypts(subRes);
      sw.stop();
      System.out.printf("[subInplace]real: [%f, %f, %f, %f]\t", ms1[0], ms1[1], ms1[2], ms1[3]);
      System.out.printf("res: [%f, %f, %f, %f]\n", res[0], res[1], res[2], res[3]);

      System.out.println(sw.prettyPrint());
    }
  }

  /**
   * Batch Pair Op
   */
  public void batchPairOp(SchemaType schemaType, int size, CurveName curveName) {
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
      sw.start("encrypt [ms11, ms12]");
      Ciphertext cts1 = pheKit.encryptPairs(ms11, ms12);
      sw.stop();
      sw.start("encrypt [ms21, ms22]");
      Ciphertext cts2 = pheKit.encryptPairs(ms21, ms22);
      sw.stop();

      sw.start("add");
      Ciphertext addRes = pheKit.adds(cts1, cts2);
      sw.stop();
      sw.start("decrypt add");
      double[] res = pheKit.decryptPairs(addRes);
      sw.stop();
      System.out.printf("[add]real: [(%f, %f), (%f, %f), (%f, %f), (%f, %f)]\t", res11[0], res12[0], res11[1], res12[1], res11[2], res12[2], res11[3], res12[3]);
      System.out.printf("res: [(%f, %f), (%f, %f), (%f, %f), (%f, %f)]\n", res[0], res[size], res[1], res[size + 1], res[2], res[size + 2], res[3], res[size + 3]);
      sw.start("addInplace");
      pheKit.addInplaces(addRes, cts2);
      sw.stop();
      sw.start("decrypt addInplace");
      res = pheKit.decryptPairs(addRes);
      sw.stop();
      System.out.printf("[addInplace]real: [(%f, %f), (%f, %f), (%f, %f), (%f, %f)]\t", res21[0], res22[0], res21[1], res22[1], res21[2], res22[2], res21[3], res22[3]);
      System.out.printf("res: [(%f, %f), (%f, %f), (%f, %f), (%f, %f)]\n", res[0], res[size], res[1], res[size + 1], res[2], res[size + 2], res[3], res[size + 3]);

      sw.start("sub");
      Ciphertext subRes = pheKit.subs(addRes, cts2);
      sw.stop();
      sw.start("decrypt sub");
      res = pheKit.decryptPairs(subRes);
      sw.stop();
      System.out.printf("[sub]real: [(%f, %f), (%f, %f), (%f, %f), (%f, %f)]\t", res11[0], res12[0], res11[1], res12[1], res11[2], res12[2], res11[3], res12[3]);
      System.out.printf("res: [(%f, %f), (%f, %f), (%f, %f), (%f, %f)]\n", res[0], res[size], res[1], res[size + 1], res[2], res[size + 2], res[3], res[size + 3]);
      sw.start("subInplace");
      pheKit.subInplaces(subRes, cts2);
      sw.stop();
      sw.start("decrypt subInplace");
      res = pheKit.decryptPairs(subRes);
      sw.stop();
      System.out.printf("[subInplace]real: [(%f, %f), (%f, %f), (%f, %f), (%f, %f)]\t", ms11[0], ms12[0], ms11[1], ms12[1], ms11[2], ms12[2], ms11[3], ms12[3]);
      System.out.printf("res: [(%f, %f), (%f, %f), (%f, %f), (%f, %f)]\n", res[0], res[size], res[1], res[size + 1], res[2], res[size + 2], res[3], res[size + 3]);

      System.out.println(sw.prettyPrint());
    }
  }

  /**
   * PubKey Test For OU
   */
  public void testOUPubKey() {
    pubKey(SchemaType.OU, CurveName.empty);
  }

  /**
   * Single Op Test For OU
   */
  public void testOUSingleOp() {
    singleOp(SchemaType.OU, CurveName.empty);
  }

  /**
   * Single Pair Op Test For OU
   */
  public void testOUSinglePairOp() {
    singlePairOp(SchemaType.OU, CurveName.empty);
  }

  /**
   * Batch Op Test For OU
   */
  public void testOUBatchOp() {
    batchOp(SchemaType.OU, 100000, CurveName.empty);
  }

  /**
   * Batch Pair Op Test For OU
   */
  public void testOUBatchPairOp() {
    batchPairOp(SchemaType.OU, 100000, CurveName.empty);
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

}
