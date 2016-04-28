package gschiegl.rmiCalcEuler.test.client;

import static org.junit.Assert.*;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import org.junit.Before;
import org.junit.Test;

import gschiegl.rmiCalcEuler.client.ComputeEulersNumber;

/**
 * Diese Ansammlung von JUnit Testcases sollen die Kommunikation mit einem Server,
 * der RMI-Anfragen erfuellt, testen.
 * 
 * Unter anderem wird getestet, ob fuer das Programm kritische Remote Objects auch 
 * tatsaechlich bei einem Lookup zurueckgeliefert werden. Wenn nicht, dann ist
 * die Ausfuehrung der Client-Komponenten nicht moeglich.
 */
public class TestClientCommunicationWithServer extends ComputeEulersNumber {

  private String hostname;
  private int port;
  
  /**
   * Bereitet eine Verbindung zu einem RMI Server vor.
   * Fuer die Ausfuehrung der Testcases wird ein offener Server
   * auf "localhost:55432" vorausgesetzt.
   */
  @Before
  public void setupConnectionDetails() {
    hostname = "localhost";
    port = 55432;
  }
  
  /**
   * Testet, ob das Ergebnis der Eulerschen Zahl (was natuerlicherweise
   * eine Annaeherung ist) eine ungefaehren Genauigkeit erfuellt.
   * Die erlaubte Ungenauigkeit ist als +/- 0.001 Abweichung von
   * einem Referenzergebnis definiert.
   * Der Sinn dieses Tests liegt darin, ein komplett falsches Ergebnis
   * (verursacht durch funktionsbeschädigend veraenderten Programmcode)
   * zu erkennen.
   */
  @Test
  public void testGetRemoteObjectFromRegistry_ValidObject() throws AccessException, RemoteException, NotBoundException {
    Object remoteObject = getObjectFromRMIRegistry(this.hostname, this.port, "ComputeEulersNumber");
    assertNotNull(remoteObject);
  }

}
