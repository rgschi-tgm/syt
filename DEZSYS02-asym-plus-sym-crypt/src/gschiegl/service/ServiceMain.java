package gschiegl.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;

import gschiegl.keys.GenerateRSAKeypair;
import gschiegl.keys.KeyUtils;
import gschiegl.ldap.LDAPConnector;
import gschiegl.logging.ConsoleLogger;

public class ServiceMain {
  
  public static void main(String[] args) {
    // Keypair generaten
    
    GenerateRSAKeypair rsakeygen = null;
    try {
      rsakeygen = new GenerateRSAKeypair();
      rsakeygen.generate();
    } catch (NoSuchAlgorithmException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    PrivateKey privkey = rsakeygen.getPrivateKey();
    PublicKey pubkey = rsakeygen.getPublicKey();
    
    // PublicKey mit Base64 codieren
    String pubkeyHashed = KeyUtils.encodeToBase64(pubkey);
    
    // Public Key ins LDAP speichern
    LDAPConnector ldapCon = new LDAPConnector();
    
    Attribute oc = new BasicAttribute("objectClass");
    oc.add("account");
    
    Attribute nameAttr = new BasicAttribute("uid", "cryptedmessageservice");
    Attribute descriptionAttr = new BasicAttribute("description", pubkeyHashed);
    
    Attributes entryAttrs = new BasicAttributes();
    entryAttrs.put(oc);
    entryAttrs.put(nameAttr);
    entryAttrs.put(descriptionAttr);
    
    DirContext pubkeyContext = null;
    try {
      // wenn es den LDAP-Entry schon gibt, wird eine NameAlreadyBoundException geworfen
      // daher probieren wir es aus, und wenn es fehlschlagt, faengt das catch()-statement es auf
      pubkeyContext = ldapCon.createSubcontext("uid=cryptedmessageservice,dc=tgm,dc=ac,dc=at", entryAttrs);
    } catch (NamingException e) {
      
      if(e instanceof NameAlreadyBoundException) {
        // statt einen neuen Eintrag hinzufuegen, wird der aktuelle bestehende Eintrag aktualisiert
        // der neue Publickey wird gesetzt
        try {
          ldapCon.updateAttribute("uid=cryptedmessageservice,dc=tgm,dc=ac,dc=at", "description", pubkeyHashed);
        } catch (NamingException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
      }
      
      
    }
    
    // auf eintreffende Client Requests warten
      // -- mit Private Key die Nachricht im RSA-Decipher loesen
      // -- Symmetrischen Schluessel aus entschluesselter Nachricht lesen
      // -- Nachricht in Service CLI einlesen fuer Client ("IP")
      // -- mit symmetrischem Schluessel von Client "IP" die Nachricht verciphern
      // -- Antwort an Client
    try {
      ServerSocket serverSock = new ServerSocket(54321);
      
      System.out.println("Waiting for incoming requests ...");
      Socket clientSock = serverSock.accept();
      System.out.println("Got client to serve!");
      
      BufferedReader socketInputStream = new BufferedReader(new InputStreamReader(clientSock.getInputStream(), Charset.forName("ISO-8859-1")));
      BufferedWriter socketOutputStream = new BufferedWriter(new OutputStreamWriter(clientSock.getOutputStream(), Charset.forName("ISO-8859-1")));
      
      // read encrypted ciphertext from inputstream
      String clientSymmetricalKeyCryptedHashed = socketInputStream.readLine();
      System.out.println(clientSymmetricalKeyCryptedHashed);
      byte[] clientSymmetricalKeyCrypted = Base64.getDecoder().decode(clientSymmetricalKeyCryptedHashed);
      
      Cipher cipherRSA = Cipher.getInstance("RSA");
      cipherRSA.init(Cipher.UNWRAP_MODE, privkey);
      Key clientSymmetricalKey = cipherRSA.unwrap(clientSymmetricalKeyCrypted, "AES", Cipher.SECRET_KEY);
//      byte[] clientSymmetricalKeyBytes = cipherRSA.doFinal(clientSymmetricalKeyCrypted);
      
      // IMPORTANT: The next three lines will cause Exceptions (InvalidKeyException) when using the unpatched original JRE, but Oracle offers a security patch to bypass the US crytography export laws.
      // It is because of these laws that this error occurs. It appears that SecretKeySpec uses a higher bitlength than AES-128, probably AES-256. This is not supported without the patch. 
//      SecretKey clientSymmetricalKey = new SecretKeySpec(clientSymmetricalKeyBytes, "AES");
      Cipher cipherAES = Cipher.getInstance("AES");
      cipherAES.init(Cipher.ENCRYPT_MODE, clientSymmetricalKey);
      
      System.out.println("Please enter the message to transmit to the client:");
      String userMessageInput = readLineFromCommandLine();
      
      byte[] userMessageInputBytes = userMessageInput.getBytes(Charset.forName("ISO-8859-1"));
      byte[] userMessageInputBytesCrypted = cipherAES.doFinal(userMessageInputBytes);
      String userMessageInputBytesCryptedHashed = Base64.getEncoder().encodeToString(userMessageInputBytesCrypted);
      
      socketOutputStream.write(userMessageInputBytesCryptedHashed+"\n");
      socketOutputStream.flush();
      Thread.sleep(10*1000);
      
      clientSock.close();
      
      serverSock.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (NoSuchAlgorithmException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (NoSuchPaddingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InvalidKeyException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IllegalBlockSizeException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (BadPaddingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  private static String readLineFromCommandLine() {
    try {
      BufferedReader cliReader = new BufferedReader(new InputStreamReader(System.in, Charset.forName("ISO-8859-1")));
      String answer = cliReader.readLine();
      
      return answer;
    } catch(IOException e) {
      ConsoleLogger.writeError("Unable to read user input from console!");
    }
    
    return null;
  }
  
}
