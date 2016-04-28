package gschiegl.rmiCalcEuler.test.client;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import org.junit.Before;
import org.junit.Test;

import gschiegl.rmiCalcEuler.compute.Compute;
import gschiegl.rmiCalcEuler.client.ComputeEulersNumber;
import gschiegl.rmiCalcEuler.client.EulersNumber;

public class TestEulersNumberSolution extends ComputeEulersNumber {

  /* Ein vorberechnetes Ergebnis der Eulerschen Zahl zum Genauigkeitsvergleich.
   * Quelle: https://de.wikipedia.org/wiki/Eulersche_Zahl */
  final double eulersNumberTenPlaces = 2.7182818284;
  
  String hostname;
  int port;
  
  Compute computeObject = null;
  
  /**
   * Stellt eine Verbindung zu einem RMI Server fuer kommende Testcases her.
   * Fuer die Ausfuehrung der Testcases wird ein offener Server
   * auf "localhost:55432" vorausgesetzt.
   */
  @Before
  public void setupRmiConnection() throws AccessException, RemoteException, NotBoundException {
    hostname = "localhost";
    port = 55432;
    computeObject = (Compute) getObjectFromRMIRegistry(hostname, port, "ComputeEulerObject");
  }
  
  public BigDecimal doCalculateEulerNumber(int places, int polyLength) throws RemoteException {
    EulersNumber calcEulerTask = new EulersNumber(places, polyLength);
    BigDecimal solution = computeObject.executeTask(calcEulerTask);
    return solution;
  }
  
  /**
   * Testet, ob das Ergebnis der Eulerschen Zahl (was natuerlicherweise eine Annaeherung ist)
   * eine ungefaehre Genauigkeit entspricht. Die erlaubte Ungenauigkeit ist als +/- 0.001 von
   * einem Referenzergebnis definiert.
   * 
   * Der Sinn dieses Tests liegt darin, ein komplett falsches Ergebnis (verursacht durch veraenderten Programmcode)
   * zu erkennen.
   */
  @Test
  public void testApproximatelyCorrectSolution() throws RemoteException {
    BigDecimal solution = doCalculateEulerNumber(5, 10);
    assertEquals(eulersNumberTenPlaces, solution.doubleValue(), 0.001);
  }

}
