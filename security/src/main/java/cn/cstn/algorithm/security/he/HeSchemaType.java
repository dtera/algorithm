package cn.cstn.algorithm.security.he;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import sun.security.util.DerInputStream;

import java.lang.reflect.Constructor;

@SuppressWarnings("unchecked")
@RequiredArgsConstructor
public enum HeSchemaType {

  PAILLIER, OU;

  private int keySize = HeKeyPairGenerator.DEFAULT_KEY_SIZE;
  private HeKeyPairGenerator keyGen;

  private final String name = name().toLowerCase();
  private final String capitalizeName = StringUtils.capitalize(name);

  public HeSchemaType keySize(int keySize) {
    this.keySize = keySize;
    return this;
  }

  @SneakyThrows
  public HeKeyPairGenerator getKeyPairGenerator() {
    if (keyGen == null) {
      Class<HeKeyPairGenerator> clazz = getClazz(name, capitalizeName, "KeyPairGenerator");
      Constructor<HeKeyPairGenerator> ctor = clazz.getConstructor(int.class);
      keyGen = ctor.newInstance(keySize);
    }
    return keyGen;
  }

  @SneakyThrows
  public HePublicKey decodeToPublicKey(DerInputStream dis) {
    Class<HePublicKey> clazz = getClazz(name, capitalizeName, "PublicKey");
    return (HePublicKey) clazz.getDeclaredMethod("decodeToPublicKey", DerInputStream.class).invoke(null, dis);
  }

  @SneakyThrows
  public HePrivateKey decodeToPrivateKey(DerInputStream dis) {
    Class<HePrivateKey> clazz = getClazz(name, capitalizeName, "PrivateKey");
    return (HePrivateKey) clazz.getDeclaredMethod("decodeToPrivateKey", DerInputStream.class)
      .invoke(null, dis);
  }

  @SneakyThrows
  private static <T> Class<T> getClazz(String name, String capitalizeName, String type) {
    return (Class<T>) Class.forName(String.format("cn.cstn.algorithm.security.he.%s.%s%s", name, capitalizeName, type));
  }

}
