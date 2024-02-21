package cn.cstn.algorithm.security.he;

import org.junit.Test;

import java.math.BigInteger;
import java.util.Random;

@SuppressWarnings("SameParameterValue")
public class HETest {

  @Test
  public void paillierTest() {
    heTest1(HeSchemaType.PAILLIER, 2048);
    heTest2(HeSchemaType.PAILLIER, 2048);
    heTest3(HeSchemaType.PAILLIER, 2048);
  }

  @Test
  public void paillierBatchTest() {
    heBatchTest(HeSchemaType.PAILLIER, 2048);
  }

  private void heBatchTest(HeSchemaType schema, int keySize) {
    HeSession session = HeSession.openSession(schema, keySize);
    Random random = new Random();
    BigInteger[] ms1 = new BigInteger[10000], ms2 = new BigInteger[10000];
    for (int i = 0; i < ms1.length; i++) {
      ms1[i] = BigInteger.valueOf(random.nextInt(10000));
      ms2[i] = BigInteger.valueOf(random.nextInt(1000));
    }
    Ciphertext[] cs1 = session.encrypt(ms1);
    Ciphertext[] cs2 = session.encrypt(ms2);
    Ciphertext[] addCipher = session.add(cs1, cs2);
    BigInteger[] addRes = session.decrypt(addCipher);
    Ciphertext[] subCipher = session.sub(cs1, cs2);
    BigInteger[] subRes = session.decrypt(subCipher);
    Ciphertext[] mulCipher = session.mulPlaintext(cs1, ms2);
    BigInteger[] mulRes = session.decrypt(mulCipher);
    for (int i = 0; i < cs1.length; i++) {
      if (i != 0 && i % (cs1.length / 10) == 0) {
        System.out.printf("[%1$d]: %2$d + %3$d = %4$d\t", i, ms1[i], ms2[i], ms1[i].add(ms2[i]));
        System.out.printf("[%1$d]: %2$d - %3$d = %4$d\t", i, ms1[i], ms2[i], ms1[i].subtract(ms2[i]));
        System.out.printf("[%1$d]: %2$d * %3$d = %4$d\t\n", i, ms1[i], ms2[i], ms1[i].multiply(ms2[i]));
        System.out.printf("\t\t\taddRes = %1$d\t\t\t\tsubRes = %2$d\t\t\t\tmulRes = %3$d\n",
          addRes[i], subRes[i], mulRes[i]);
      }
    }
  }

  private void heTest3(HeSchemaType schema, int keySize) {
    HeKeyPairGenerator keyGen = schema.keySize(keySize).getKeyPairGenerator();
    HeKeyPair keyPair = keyGen.generateKeyPair();
    HeSession session = HeSession.openSession(keyPair.getPublicKey().getEncoded(), keyPair.getPrivateKey().getEncoded());
    heSessionTest(session);
  }

  private void heTest2(HeSchemaType schema, int keySize) {
    HeKeyPairGenerator keyGen = schema.keySize(keySize).getKeyPairGenerator();
    HeKeyPair keyPair = keyGen.generateKeyPair();
    HeSession session = HeSession.openSession(keyPair.getPublicKey(), keyPair.getPrivateKey());
    heSessionTest(session);
  }

  private void heTest1(HeSchemaType schema, int keySize) {
    HeSession session = HeSession.openSession(schema, keySize);
    heSessionTest(session);
  }

  private void heSessionTest(HeSession session) {
    System.out.println("====================encrypt & decrypt====================");
    Random random = new Random();
    int m = random.nextInt(10000);
    Ciphertext cipher = session.encrypt(BigInteger.valueOf(m));
    System.out.printf("m = %d \t decrypt_m = %s\n", m, session.decrypt(cipher));
    m = -m;
    System.out.printf("m = %d \t decrypt_m = %s\n", m, session.decrypt(session.encrypt(BigInteger.valueOf(m))));
    System.out.println("========================evaluator========================");
    int m1 = random.nextInt(10000), m2 = random.nextInt(1000);
    Ciphertext c1 = session.encrypt(BigInteger.valueOf(m1));
    Ciphertext c2 = session.encrypt(BigInteger.valueOf(m2));
    Ciphertext addCipher = session.add(c1, c2);
    Ciphertext subCipher = session.sub(c1, c2);
    Ciphertext mulPlianCipher = session.mulPlaintext(c1, BigInteger.valueOf(m2));
    int add = m1 + m2, sub = m1 - m2, mulPlain = m1 * m2;
    BigInteger addDecrypted = session.decrypt(addCipher), subDecrypted = session.decrypt(subCipher),
      mulPlainDecrypted = session.decrypt(mulPlianCipher);
    System.out.printf("m1= %d \t\t\t m2 = %d\n", m1, m2);
    System.out.printf("m1 + m2 = %d \t\t c1 + c2 = %d\n", add, addDecrypted);
    System.out.printf("m1 - m2 = %d \t\t c1 - c2 = %d\n", sub, subDecrypted);
    System.out.printf("m1 * m2 = %d \t c1 * c2 = %d\n\n", mulPlain, mulPlainDecrypted);
    assert add == addDecrypted.intValue();
    assert sub == subDecrypted.intValue();
    assert mulPlain == mulPlainDecrypted.intValue();
  }

}
