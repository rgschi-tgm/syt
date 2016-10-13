package gschiegl.keys;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class GenerateRSAKeypair {

  private KeyPairGenerator keygenInstance = null;
  
  private KeyPair keypair = null;
  private PublicKey pubkey = null;
  private PrivateKey privatekey = null;
  
  /**
   * Prepairs for generating a 2048-bit RSA keypair.
   * @throws NoSuchAlgorithmException   Whenever the current version of Java does not support RSA.
   */
  public GenerateRSAKeypair() throws NoSuchAlgorithmException {
    this.keygenInstance = KeyPairGenerator.getInstance("RSA");
    this.keygenInstance.initialize(2048);
  }
  
  /**
   * Generates the 2048-bit RSA keypair.
   */
  public void generate() {
    this.keypair = this.keygenInstance.generateKeyPair();
    this.pubkey = keypair.getPublic();
    this.privatekey = keypair.getPrivate();
  }
  
  public PrivateKey getPrivateKey() {
    return this.privatekey;
  }
  
  public PublicKey getPublicKey() {
    return this.pubkey;
  }
  
  /**
   * Writes a Base64 encoded version of the public key to the given File (Path).
   * The encoded version can later be restored to an instance of PublicKey.
   * @param f   The target to write the hashed version to.
   * @throws IOException
   */
  public void savePubkeyToFile(Path f) throws IOException {
    BufferedWriter writer = Files.newBufferedWriter(f, Charset.forName("ISO-8859-1"));
    
    byte[] pubkeyBytes = this.pubkey.getEncoded();
    String pubkeyHashed = Base64.getEncoder().encodeToString(pubkeyBytes);
    
    writer.write(pubkeyHashed);
  }
  
  /**
   * Writes a Base64 encoded version of the private key to the given File (Path).
   * The encoded version can later be restored to an instance of PrivateKey.
   * @param f   The target to write the hashed version to.
   * @throws IOException
   */
  public void savePrivatekeyToFile(Path f) throws IOException {
    BufferedWriter writer = Files.newBufferedWriter(f, Charset.forName("ISO-8859-1"));
    
    byte[] privatekeyByte = this.privatekey.getEncoded();
    String privatekeyHashed = Base64.getEncoder().encodeToString(privatekeyByte);
    
    writer.write(privatekeyHashed);
  }
  
}
