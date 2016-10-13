package gschiegl.keys;

import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyUtils {

  public static String encodeToBase64(Key key) {
    byte[] keyBytes = key.getEncoded();
    String hashedKey = Base64.getEncoder().encodeToString(keyBytes);
    
    return hashedKey;
  }
  
  public static PrivateKey decodeToPrivateKey(String hashedKey) {
    byte[] keyBytes = Base64.getDecoder().decode(hashedKey);
    
    X509EncodedKeySpec keyspec = new X509EncodedKeySpec(keyBytes);
    KeyFactory kf = null;
    try {
      kf = KeyFactory.getInstance("RSA");
    } catch (NoSuchAlgorithmException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    
    PrivateKey privatekeys = null;
    try {
      privatekeys = kf.generatePrivate(keyspec);
    } catch (InvalidKeySpecException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    return privatekeys;
  }
  
  public static PublicKey decodeToPublicKey(String hashedKey) {
    byte[] keyBytes = Base64.getDecoder().decode(hashedKey);
    
    X509EncodedKeySpec keyspec = new X509EncodedKeySpec(keyBytes);
    KeyFactory kf = null;
    try {
      kf = KeyFactory.getInstance("RSA");
    } catch (NoSuchAlgorithmException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    
    PublicKey pubkeys = null;
    try {
      pubkeys = kf.generatePublic(keyspec);
    } catch (InvalidKeySpecException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    return pubkeys;
  }
  
}
