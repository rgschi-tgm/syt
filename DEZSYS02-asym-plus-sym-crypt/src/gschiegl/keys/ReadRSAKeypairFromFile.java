package gschiegl.keys;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class ReadRSAKeypairFromFile {

  KeyPair keypair = null;
  PublicKey publickey = null;
  PrivateKey privatekey = null;
  
  public ReadRSAKeypairFromFile() {
    
  }
  
  /**
   * Reads (ONLY!) the first line of the given file and translates the characters found into
   * an instance of PrivateKey. The resulting key is an object member of this class, therefore
   * you need to call the respective get() method.
   * @param f   The Path to the file to read from.
   * @throws IOException
   * @throws NoSuchAlgorithmException   Thrown when the system's Java implementation does not support RSA.
   */
  public void readPrivateKey(Path f) throws IOException, NoSuchAlgorithmException {
    BufferedReader reader = Files.newBufferedReader(f, Charset.forName("ISO-8859-1"));
    
    String privatekeyHashed = reader.readLine();
    byte[] privatekeyBytes = Base64.getDecoder().decode(privatekeyHashed);
    
    X509EncodedKeySpec keyspec = new X509EncodedKeySpec(privatekeyBytes);
    KeyFactory kf = KeyFactory.getInstance("RSA");
    
    try {
      this.privatekey = kf.generatePrivate(keyspec);
    } catch (InvalidKeySpecException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  /**
   * Reads (ONLY!) the first line of the given file and translates the characters found into
   * an instance of PublicKey. The resulting key is an object member of this class, therefore
   * you need to call the respective get() method.
   * @param f   The Path to the file to read from.
   * @throws IOException
   * @throws NoSuchAlgorithmException   Thrown when the system's Java implementation does not support RSA.
   */
  public void readPublicKey(Path f) throws IOException, NoSuchAlgorithmException {
BufferedReader reader = Files.newBufferedReader(f, Charset.forName("ISO-8859-1"));
    
    String privatekeyHashed = reader.readLine();
    byte[] privatekeyBytes = Base64.getDecoder().decode(privatekeyHashed);
    
    X509EncodedKeySpec keyspec = new X509EncodedKeySpec(privatekeyBytes);
    KeyFactory kf = KeyFactory.getInstance("RSA");
    
    try {
      this.publickey = kf.generatePublic(keyspec);
    } catch (InvalidKeySpecException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public PublicKey getPublickey() {
    return publickey;
  }

  public PrivateKey getPrivatekey() {
    return privatekey;
  }
  
  
  
}
