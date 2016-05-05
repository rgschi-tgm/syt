package helloworld.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import gschiegl.CalculatorPackage.DivisionByZeroException;
import helloworld.CalculatorClient;

public class TestCalculatorClient {

	CalculatorClient calculator = null;
	
	/**
	 * Stellt eine Verbindung zum ORB her und ruft das verteilte Objekt "Echo" ab.
	 * Die Verbindung muss derzeit statisch hergestellt werden, weil die Kommunikation
	 * zwischen Ant und Java via System-Properties nicht funktioniert (keine Properties
	 * werden gesetzt, obwohl in Ant sysproperty gesetzt sind).
	 * 
	 * Damit dieser Test nicht fehlschlaegt, obwohl kein Fehler aufgetreten sein sollte,
	 * muss der ORB via localhost:2809 und Bezeichnung *NameService* verfuegbar sein.
	 * 
	 * Testet dabei indirekt auch gleich, ob die benoetigten Resourcen zur Verfuegung stehen.
	 */
	@Before
	public void calculatorSetup() throws InvalidName, NotFound, CannotProceed, org.omg.CosNaming.NamingContextPackage.InvalidName {		
		String[] args = new String[]{ "-ORBInitRef", "NameService=corbaloc::127.0.0.1:2809/NameService" };
		calculator = new CalculatorClient(args);
		calculator.connect();
	}
	
	/**
	 * Testet, ob der Rechner eine einfache Rechnung korrekt wie erwartet
	 * berechnet.
	 */
	@Test
	public void testAddValidNumbersValidResult() throws Exception {
		assertEquals(2, calculator.add(1, 1));
	}
	
	/**
	 * Testet, ob bei einer Divison durch Null wie erwartet eine dementsprechende
	 * Exception geworfen wird.
	 */
	@Test (expected = DivisionByZeroException.class)
	public void testDivisionByZero() throws Exception {
		calculator.divide(5, 0);
	}

}
