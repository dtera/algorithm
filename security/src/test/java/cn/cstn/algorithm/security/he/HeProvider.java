package cn.cstn.algorithm.security.he;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.util.Iterator;
import java.util.LinkedHashSet;

@SuppressWarnings("removal")
public class HeProvider extends Provider {

  private LinkedHashSet<Service> services;

  @SuppressWarnings("SameParameterValue")
  private void add(String type, String algo, String cn) {
    services.add(new Provider.Service(this, type, algo, cn, null, null));
  }

  public HeProvider() {
    super("HE", 1.0, "Homomorphic Encryption Provider");

    add("KeyPairGenerator", "Paillier", "cn.cstn.algorithm.security.he.paillier.PaillierKeyPairGenerator");
    // add("KeyPairGenerator", "OU", "cn.cstn.algorithm.security.he.ou.OUKeyPairGenerator");

    Iterator<Provider.Service> serviceIter = services.iterator();

    if (System.getSecurityManager() == null) {
      putEntries(serviceIter);
    } else {
      AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
        putEntries(serviceIter);
        return null;
      });
    }
  }

  void putEntries(Iterator<Provider.Service> i) {
    while (i.hasNext()) {
      putService(i.next());
    }
  }

}
