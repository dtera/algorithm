package cn.cstn.algorithm.security.rsa;

import org.junit.Test;

import javax.crypto.Cipher;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSATest {

  @Test
  public void generateKeyPair() throws Exception {
    KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
    keyGen.initialize(1024);
    KeyPair keyPair = keyGen.generateKeyPair();
    try (FileOutputStream fos = new FileOutputStream("id_rsa.pub")) {
      fos.write(keyPair.getPublic().getEncoded());
    }
    try (FileOutputStream fos = new FileOutputStream("id_rsa")) {
      fos.write(keyPair.getPrivate().getEncoded());
    }
  }

  @Test
  public void encryptDecrypt() throws Exception {
    File pubKeyFile = new File("id_rsa.pub");
    File priKeyFile = new File("id_rsa");
    if (!(pubKeyFile.exists() && priKeyFile.exists())) {
      generateKeyPair();
    }
    byte[] pubKeyBytes = Files.readAllBytes(pubKeyFile.toPath());
    byte[] priKeyBytes = Files.readAllBytes(priKeyFile.toPath());
    EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(pubKeyBytes);
    EncodedKeySpec priKeySpec = new PKCS8EncodedKeySpec(priKeyBytes);
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");

    PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);
    PrivateKey priKey = keyFactory.generatePrivate(priKeySpec);

    Cipher encryptCipher = Cipher.getInstance("RSA");
    String msg = "Hello";
    encryptCipher.init(Cipher.ENCRYPT_MODE, pubKey);
    byte[] encryptedBytes = encryptCipher.doFinal(msg.getBytes(StandardCharsets.UTF_8));

    Cipher decryptCipher = Cipher.getInstance("RSA");
    decryptCipher.init(Cipher.DECRYPT_MODE, priKey);
    byte[] decryptedBytes = decryptCipher.doFinal(encryptedBytes);
    System.out.println(new String(decryptedBytes, StandardCharsets.UTF_8));
  }

}
