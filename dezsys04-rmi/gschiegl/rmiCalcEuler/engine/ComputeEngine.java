package gschiegl.rmiCalcEuler.engine;

import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Logger;

import gschiegl.rmiCalcEuler.compute.Compute;

public class ComputeEngine {

  /* Logger-Objekt generieren */
  private static Logger logger = Logger.getLogger(ComputeEngine.class.getName());
  
  public static void main(String[] args) {
    if(System.getSecurityManager()==null) {
      System.setSecurityManager(new SecurityManager());
    }
    
    /* Programmoptionen auslesen */
    if(args.length == 0) {
      logger.severe("Keine Programmoptionen angegeben! Breche ab.");
      logger.info("ComputeEngine -p <port>");
      System.exit(1);
    }
    
    String portString = args[0];
    int port = -1;
    try {
      port = Integer.parseInt(portString);
    } catch (NumberFormatException e) {
      logger.severe("Die Portangabe darf nur eine ganzzahlige positive Nummer sein! Breche ab.");
      System.exit(1);
    }
    
    
    /** Remote Object mit RMI-Registry registrieren */
    Registry regRmi = null;
    try {
      
      /* Objekt bei RMI Namensdienst der JVM registrieren */
      regRmi = LocateRegistry.createRegistry(port);
      Compute engine = new ComputeImpl();
      Compute stub = (Compute) UnicastRemoteObject.exportObject(engine, 0);
      
      regRmi.rebind("ComputeEulersNumber", stub);
      
      
      //TODO: Java Logging System
      logger.info("Warte auf eintreffende Kommandos!");
      
      
      logger.info("Druecken Sie auf ENTER um das Programm zu beenden!");
      try {
        while ( System.in.read() != '\n' ); // bleib in der unendlichlangen Schleife, bis Benutzer ENTER tippt
      } catch (IOException e) {
        logger.severe("Fehler beim Lesen von der Konsole aufgetreten!");
      }
        
      
      /* Registrierung beim RMI Namensdienst aufheben */
      UnicastRemoteObject.unexportObject(engine, true);
      
    } catch(AccessException e) {
      logger.severe("Der Namensdienst verweigert Kommunikation aufgrund fehlender Berechtigungen!)");
      System.exit(1);
    } catch(RemoteException e) {
      logger.severe("Ein Fehler bei der Kommunikation mit dem Namensdienst ist aufgetreten!");
      logger.severe("Moeglicherweise liegt das daran, dass das Programm zwei Mal gestartet wurde!");
      System.exit(1);
    }
  }
}
