package gschiegl.ldap;

import javax.naming.*;
import javax.naming.directory.*;

import java.util.Hashtable;

/**
 * Demonstrates how to create an initial context to an LDAP server using simple
 * authentication.
 *
 * usage: java Simple
 */
public class LDAPConnector {

  // TODO: LDAP name service konfigurieren
  // TODO: Auslagern auf Properties-Objekt
  private static String host = "192.168.6.129";
  private static int port = 389;
  private static String auth_user = "cn=admin,dc=tgm,dc=ac,dc=at";
  private static String auth_password = "password";
  private static DirContext ctx = null;

  private Hashtable<String, Object> ldapEnvConfiguration;
  
  public LDAPConnector() {
    // Set up environment for creating initial context
    ldapEnvConfiguration = new Hashtable<String, Object>(11);

    ldapEnvConfiguration.put(Context.INITIAL_CONTEXT_FACTORY,
        "com.sun.jndi.ldap.LdapCtxFactory");
    ldapEnvConfiguration.put(Context.PROVIDER_URL, "ldap://" + host + ":" + port);

    // Authenticate
    ldapEnvConfiguration.put(Context.SECURITY_AUTHENTICATION, "simple");
    ldapEnvConfiguration.put(Context.SECURITY_PRINCIPAL, auth_user);
    ldapEnvConfiguration.put(Context.SECURITY_CREDENTIALS, auth_password);

    try {

      // Create initial context
      ctx = new InitialDirContext(ldapEnvConfiguration);

    } catch (NamingException e) {
      e.printStackTrace();
    }

  }

  public static void printSearchResult(NamingEnumeration namingEnum) {
    try {
      while (namingEnum.hasMore()) {
        SearchResult sr = (SearchResult) namingEnum.next();
        String name = sr.getName();
        String description = sr.getAttributes().get("description") != null
            ? sr.getAttributes().get("description").toString() : "";
        System.out.println(">>>" + name + " " + description);
      }
    } catch (NamingException e) {
      e.printStackTrace();
    }
  }

  public NamingEnumeration search(String inBase, String inFilter)
      throws NamingException {

    // Create default search controls
    SearchControls ctls = new SearchControls();

    // Specify the search filter to match
    // Ask for objects with attribute sn == Geisel and which have
    // the "mail" attribute.
    String filter = "";

    // Search for objects using filter
    return ctx.search(inBase, inFilter, ctls);
  }
  
  /**
   * Search for entries that fit the attributes given via the second parameter.
   * Only entries that fit the attributes AND are in the given context (1st parameter)
   * are returned. Only entry attributes whos specifiers (names) were given in the third parameter
   * are collected off of the matching entries.
   * @param context
   * @param attributesToSearchFor
   * @return
   */
  public NamingEnumeration<SearchResult> search(String context, Attributes filter, String[] attributesToReturn) {
    try {
      return ctx.search(context, filter, attributesToReturn);
    } catch (NamingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    return null;
  }
  
  public DirContext createSubcontext(String entryDN, Attributes entryAttrs) throws NamingException {
    DirContext createdContext = ctx.createSubcontext(entryDN, entryAttrs);
    
    return createdContext;
  }

  public void updateAttribute(String inDN, String inAttribute,
      String inValue) throws NamingException {

    ModificationItem[] mods = new ModificationItem[1];
    
    // neue Veraenderung an EINEM ATTRIBUT, inAttribute = Name des Attributes, inValue = neuer Wert fuer das Attribut
    Attribute mod0 = new BasicAttribute(inAttribute, inValue);
    mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, mod0); // es wird einfach ueberschrieben
    
    
    // inDN = der "Primary Key" zu einem LDAP-Eintrag, mods = alle Veraenderungen an dem ausgewaehlten LDAP-Eintrag
    ctx.modifyAttributes(inDN, mods);

  }

}
