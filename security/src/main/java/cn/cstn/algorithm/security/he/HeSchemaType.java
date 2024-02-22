package cn.cstn.algorithm.security.he;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import sun.security.util.DerInputStream;

import java.lang.reflect.Constructor;

@SuppressWarnings("unchecked")
@RequiredArgsConstructor
public enum HeSchemaType {

  PAILLIER, OU, OU_V1;

  private int keySize = HeAbstractKeyPairGenerator.DEFAULT_KEY_SIZE;
  private HeAbstractKeyPairGenerator keyGen;

  private final int version;
  private final String name;
  private final String capitalizeName;

  HeSchemaType() {
    String[] ts = name().toLowerCase().split("_");
    version = ts.length > 1 ? Integer.parseInt(ts[1].substring(1)) : 0;
    name = ts[0];
    capitalizeName = StringUtils.capitalize(name);
  }

  public HeSchemaType keySize(int keySize) {
    this.keySize = keySize;
    return this;
  }

  @SneakyThrows
  public HeAbstractKeyPairGenerator getKeyPairGenerator() {
    if (keyGen == null) {
      Class<HeAbstractKeyPairGenerator> clazz = getClazz(name, capitalizeName, "KeyPairGenerator");
      Constructor<HeAbstractKeyPairGenerator> ctor = clazz.getConstructor(int.class, int.class);
      keyGen = ctor.newInstance(keySize, version);
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
