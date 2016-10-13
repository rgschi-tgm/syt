package gschiegl.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.SearchResult;

import gschiegl.keys.KeyUtils;
import gschiegl.ldap.LDAPConnector;

public class ClientMain {

    public static void main(String[] args) {
      
      // get the public key of the 'cryptedmessageservice'
      LDAPConnector ldapCon = new LDAPConnector();
      Attributes attrsFilter = new BasicAttributes();
      Attribute attrCommonName = new BasicAttribute("uid", "cryptedmessageservice");
      attrsFilter.put(attrCommonName);
      String[] attrsToReturn = new String[]{"description"};
      NamingEnumeration<SearchResult> ldapResult = ldapCon.search("dc=tgm,dc=ac,dc=at", attrsFilter, attrsToReturn);
      
      String messagePartnerPubkeyHashed = null;
      try {
        while(ldapResult.hasMore()) {
          SearchResult currentLdapEntry = ldapResult.next();
          Attribute descriptionAttr = currentLdapEntry.getAttributes().get("description");
          String descriptionString = descriptionAttr.get(0).toString();
          
          System.out.println("We got the public key: "+descriptionString);
          
          messagePartnerPubkeyHashed = descriptionString;
        }
      } catch (NamingException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      
      // convert hashed public key of partner to PublicKey object
      PublicKey pubkeyPartner = KeyUtils.decodeToPublicKey(messagePartnerPubkeyHashed);
      
      
      // generate a symmetrical key for this client
      KeyGenerator symmetricalKeygen = null;
      try {
        symmetricalKeygen = KeyGenerator.getInstance("AES");
      } catch (NoSuchAlgorithmException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      SecretKey symmetricalKey = symmetricalKeygen.generateKey();
//      String symmetricalKeyHashed = KeyUtils.encodeToBase64(symmetricalKey); // encode key to base64 string since we need to transmit it over the network anyways
      
      // transmit symmetrical key to service
      Socket socketToMessageService = null;
      try {
        socketToMessageService = new Socket("127.0.0.1", 54321);
        
        // set up a Cipher object to use the partner's public key for encryption
        Cipher cipherObj = Cipher.getInstance("RSA");
//        cipherObj.init(Cipher.PUBLIC_KEY, pubkeyPartner);
        cipherObj.init(Cipher.WRAP_MODE, pubkeyPartner);
        byte[] symmetricalKeyCryptedBytes = cipherObj.wrap(symmetricalKey);
        
        // encode symmetrical key
//        byte[] cryptedSymmetricalKey = cipherObj.doFinal(symmetricalKeyHashed.getBytes(Charset.forName("ISO-8859-1")));
        String symmetricalKeyCrypted = Base64.getEncoder().encodeToString(symmetricalKeyCryptedBytes);
        
        // transmit the ciphertext via the Socket
        OutputStreamWriter osWriter = new OutputStreamWriter(socketToMessageService.getOutputStream(), Charset.forName("ISO-8859-1"));
        osWriter.write(symmetricalKeyCrypted+"\n");
        osWriter.flush();
        System.out.println("Sent crypted message to service ...");
        
        // read crypted message from service (symmetrical key)
        BufferedReader osReader = new BufferedReader(new InputStreamReader(socketToMessageService.getInputStream(), Charset.forName("ISO-8859-1")));
        
        // read encrypted answer from service, encrypted with our symmetrical key
        String answerService = osReader.readLine(); // message should be Base64 encoded ciphertext
        
        // close connection to the server
        socketToMessageService.close();
  
        // decode ciphertext
        byte[] answerDecodedBytes = Base64.getDecoder().decode(answerService);
        
        Cipher cipherObjAES = Cipher.getInstance("AES");
        cipherObjAES.init(Cipher.DECRYPT_MODE, symmetricalKey);
        byte[] answerDecryptedBytes = cipherObjAES.doFinal(answerDecodedBytes);
        
        // encode byte[] to String via charset 'iso-8859-1'
        String answerDecrypted = new String(answerDecryptedBytes, Charset.forName("ISO-8859-1"));
        System.out.println("Service answered:");
        System.out.println(answerDecrypted);
        
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
      } 
      
      
      
    }
}
