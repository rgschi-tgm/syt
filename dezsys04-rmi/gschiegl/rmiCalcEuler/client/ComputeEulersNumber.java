package gschiegl.rmiCalcEuler.client;

import java.math.BigDecimal;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;

import gschiegl.rmiCalcEuler.compute.Compute;

public class ComputeEulersNumber {

  /* Logger-Objekt generieren */
  private static Logger logger = Logger.getLogger(ComputeEulersNumber.class.getName());
  
  public static void main(String[] args) {
    /* Initialisiere Java Logging System */
    logger.setLevel(Level.INFO);
    
    /* Initialisiere einen neuen SecurityManager, falls aktuell keiner vorhanden */
    if(System.getSecurityManager()==null) {
      System.setSecurityManager(new SecurityManager());
    }
    
    /**
     * Programmargumente auslesen 
     */
    
    /* moegliche Optionen */
    Option optHostname = Option.builder("h")
        .argName("hostname")
        .desc("Der Hostname oder die IP der RMI Middleware.")
        .hasArg()
        .numberOfArgs(1)
        .required()
        .build();
    
    Option optPort = Option.builder("p")
        .argName("port")
        .desc("Der Port der RMI Middleware.")
        .hasArg()
        .numberOfArgs(1)
        .required()
        .build();
    
    Option optDecimalPlaces = Option.builder("n")
        .argName("decimalplaces")
        .desc("Anzahl der Nachkommastellen, die berechnet werden sollen.")
        .hasArg()
        .numberOfArgs(1)
        .required()
        .build();
    
    Options programOptions = new Options();
    programOptions.addOption(optHostname);
    programOptions.addOption(optPort);
    programOptions.addOption(optDecimalPlaces);
    
    /* Vom Benutzer mitgelieferte Programmargumente 
     * gegen moegliche Optionen parsen */
    DefaultParser optionParser = new DefaultParser();
    CommandLine optionResultCL = null;
    try {
       optionResultCL = optionParser.parse(programOptions, args);
    } catch (ParseException e) { 
      logger.severe("Ihre angegebenen Programmoptionen konnten nicht fehlerfrei verarbeitet werden! Breche ab.");
      printProgramUsage(programOptions);
      System.exit(1);
    }
    
    /* Mitgelieferte Optionen auslesen */
    String hostname = optionResultCL.getOptionValue('h');
    String portString = optionResultCL.getOptionValue('p');
    int port = -1;
    String decimalPlacesString = optionResultCL.getOptionValue('n');
    int amountDecimalPlaces = -1;
    if(hostname == null || portString == null || decimalPlacesString == null) {
      printProgramUsage(programOptions);
    }
        
    // Port in Integer parsen
    try {
      port = Integer.parseInt(portString);
      amountDecimalPlaces = Integer.parseInt(decimalPlacesString);
    } catch(NumberFormatException e) {
      logger.severe("Die Angabe des Ports oder der Anzahl an Nachkommastellen ist nicht gueltig! Nur positive Ganzzahlen duerfen enthalten sein!");
      System.exit(1);
    }
    
    /**
     * Berechnung der Eulerschen Zahl mit dem Remote Object, welches Berechnung abnimmt.
     */
    Compute eulersNumberCalculator = null;
    try {
       // Remote Object ueber RMI aus der Registry holen, das fuer die Ausfuehrung der Task-Anfrage zustaendig ist
       eulersNumberCalculator = (Compute) getObjectFromRMIRegistry(hostname, port, "ComputeEulersNumber");

       // Berechnung der Eulerschen Zahl durchfuehren
       BigDecimal solutionEulersNumber = initiateEulersNumberCalculation(eulersNumberCalculator, amountDecimalPlaces);
       
       // Ausgabe des berechneten Eulerschen Zahl
       System.out.println(solutionEulersNumber.toString());
       
    } catch (AccessException e) {
      logger.severe("Ein kritischer Berechtigungs-Fehler ist aufgetreten! Breche ab.");
      System.exit(1);
    } catch (RemoteException e) {
      logger.severe("Bei der Kommunikation mit dem Berechnungsserver ist ein kritischer Fehler aufgetreten! Breche ab.");
      System.exit(1);
    } catch (NotBoundException e) {
      logger.severe("Ein technischer Fehler ist aufgetreten (Typ: entferntes Objekt ist nicht registriert)! Breche ab.");
      System.exit(1);
    }
    
    
  }
  
  protected static BigDecimal initiateEulersNumberCalculation(Compute computeObject, int amountDecimalPlaces) throws RemoteException {
    // Objekt erzeugen, das spaeter vom Server fuer die execute()-Methode verwendet wird
    EulersNumber task = new EulersNumber(amountDecimalPlaces, 10);
    
    // ruft die Ausfuehrungsmethode fuer den Auftrag auf, der vom Stub-Objekt (skeleton) zum richtigen
    // Objekt am Server uebertragen wird, dort den Auftrag bearbeitet und das Ergebnis zurueckliefert.       
    BigDecimal calculatedEulersNumber = computeObject.executeTask(task);
    
    return calculatedEulersNumber;
  }

  /**
   * Versucht, den ueber die im Parameter angegebenen Verbindungsdaten erreichbaren RMI-Registryserver
   * um das Remote Object abzufragen, wessen Name im Parameter uebergeben wurde.
   * 
   * Wenn das erfolgreich war, wird dieses Remote Object zurueckgegeben.
   * 
   * @param hostname      Der Hostname bzw. die IP, unter der die RMI-Registry kontaktiert werden kann.
   * @param port          Der Port, ueber den die RMI-Registy kontaktiert werden kann.
   * @param lookupName    Der Name des Remote Objects, das abgerufen werden soll.
   * @return              Das Remote Object (bzw. ein damit verbundenes Stub-Objekt).
   */
  protected static Object getObjectFromRMIRegistry(String hostname, int port, String lookupName) throws RemoteException, AccessException, NotBoundException{
    Registry rmiReg = null;
    Compute eulersNumberCalculator = null;
    
    rmiReg = LocateRegistry.getRegistry(hostname, port);
    eulersNumberCalculator = (Compute) rmiReg.lookup("ComputeEulersNumber");
    
    return eulersNumberCalculator;
  }
  
  private static void printProgramUsage(Options options) {
    HelpFormatter helpFormat = new HelpFormatter();
    helpFormat.printHelp("ComputeEulersNumber -h <hostname> -p <port> -n <amountDecimalPlaces>", options);
  }
  
}
