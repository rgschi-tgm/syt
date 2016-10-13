package gschiegl.keys;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class TestKeyPairGenerator {

  public static void main(String[] args) {
    try {
      /* get instance of the keypair generator for the RSA algorithm, 2048 bit */ 
      KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
      keygen.initialize(2048);
    
      /* generate a new RSA keypair and receive an instance of PublicKey and PrivateKey */
      KeyPair keypair = keygen.generateKeyPair();
      PublicKey pubkey = keypair.getPublic();
      PrivateKey privatekey = keypair.getPrivate();
      
      /* get the byte[] version of the Key objects */
      byte[] pubkeyBytes = pubkey.getEncoded();
      byte[] privatekeyBytes = privatekey.getEncoded();
      
      /* Base64 encode the byte[] version of the PublicKey/PrivateKey */
      String pubkeyHashedString = Base64.getEncoder().encodeToString(pubkeyBytes);
      String privatekeyHashedString = Base64.getEncoder().encodeToString(privatekeyBytes);

      System.out.println("pubkey follows");
      System.out.println(pubkeyHashedString);

      System.out.println("privatekey follows");
      System.out.println(privatekeyHashedString);
      
      /**
       * Decode the Base64 version of the PublicKey and convert it back to
       * an instance of PublicKey.
       */
      byte[] decodedPubkey = Base64.getDecoder().decode(pubkeyHashedString);
      X509EncodedKeySpec pubkeySpec = new X509EncodedKeySpec(decodedPubkey);
      KeyFactory kf = KeyFactory.getInstance("RSA");
      PublicKey pubkeyClone = kf.generatePublic(pubkeySpec);
      
//      System.out.println(pubkey.equals(pubkeyClone));
      
//      String pubkeyString = new String(pubkeyBytes, Charset.forName("ISO-8859-1"));
//      String privatekeyString = new String(privatekeyBytes, Charset.forName("ISO-8859-1"));
//      
//      /* writing bytes of privateKey to file */
//      File privatekeyFile = new File("privatekey_test");
//      BufferedWriter privatekeyWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(privatekeyFile)));
//      privatekeyWriter.write(privatekeyString);
//      privatekeyWriter.flush();
//      privatekeyWriter.close();
//      
//      /* writing bytes of pubKey to file */
//      File pubkeyFile = new File("pubkey_test");
//      BufferedWriter pubkeyWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pubkeyFile)));
//      pubkeyWriter.write(pubkeyString);
//      pubkeyWriter.flush();
//      pubkeyWriter.close();
//      
//      System.out.println("Written to file.");
    
    } catch (NoSuchAlgorithmException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InvalidKeySpecException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
  }
  
}
