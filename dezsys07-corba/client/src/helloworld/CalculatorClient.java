package helloworld;

import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import gschiegl.Calculator;
import gschiegl.CalculatorHelper;
import gschiegl.CalculatorPackage.DivisionByZeroException;

import java.util.logging.Logger;

import org.omg.CORBA.*;
import org.omg.CORBA.Object;
import org.omg.CORBA.ORBPackage.InvalidName;

/**
 * @author Hagen Aad Fock <hagen.fock@gmail.com>
 * @version 13.03.2015
 * 
 * Ruft die Echo Methode des C++ Servers auf und gibt einen String auf der Konsole aus.
 * Sollte ein Fehler aufgetreten sein, so wird eine Exception geworfen und eine Fehlermeldung zusammen mit dem Stracktrace auf der Konsole ausgegeben.
 */
public class CalculatorClient {
	
	private String[] orbArgs = null;
	private Calculator calc = null;
	
	public CalculatorClient(String[] args) {
		this.orbArgs = args;
	}
	
	public void connect() throws InvalidName, NotFound, CannotProceed, org.omg.CosNaming.NamingContextPackage.InvalidName {
		/* Erstellen und intialisieren des ORB */
		ORB orb = ORB.init(this.orbArgs, null);
		
		/* Erhalten des RootContext des angegebenen Namingservices */
		Object o = orb.resolve_initial_references("NameService");
		
		/* Verwenden von NamingContextExt */
		NamingContextExt rootContext = NamingContextExtHelper.narrow(o);
		
		/* Angeben des Pfades zum Echo Objekt */
		NameComponent[] name = new NameComponent[2];
		name[0] = new NameComponent("test","my_context");
		name[1] = new NameComponent("Echo", "Object");
		
		/* Aufloesen der Objektreferenzen */
		this.calc = CalculatorHelper.narrow(rootContext.resolve(name));
	}
	
	private void checkIfAlreadyConnected() throws Exception {
		if(this.calc == null) {
			throw new Exception("Not connected to calculator!");
		}
	}
	
	public int add(int num1, int num2) throws Exception {
		checkIfAlreadyConnected();
		
		return this.calc.add(num1, num2);
	}
	
	public double divide(int dividend, int divisor) throws Exception {
		checkIfAlreadyConnected();
		
		return this.calc.divide(dividend, divisor);
	}
	
	public static void main(String[] args)  {
		Logger log = Logger.getLogger("CalculatorClient");
		CalculatorClient calculator = new CalculatorClient(args);
		try {
			calculator.connect();
			
			/** Rechnen mit verteiltem Calculator-Objekt */
			int summand1 = 1;
			int summand2 = 1;
			int resultSum = calculator.add(summand1, summand2);
			System.out.println("The result of "+summand1+"+"+summand2+" equals: "+resultSum);
			
			try{
				int dividend = 5;
				int divisor = 5;
				double resultDivision = calculator.divide(dividend, divisor);
				
				System.out.println("The result of "+dividend+"/"+divisor+" equals: "+resultDivision);
			} catch(DivisionByZeroException e) {
				System.err.println("An error occured: "+e.cause);
			}

		}	catch (InvalidName e)	{
			log.severe("Cannot connect to server's NameService!");
		} catch (NotFound e1) {
			log.severe("Could not access to remote calculator: Not found!");
		} catch (CannotProceed e1) {
			log.severe("Remote server closed connection: Cannot proceed!");
		} catch (org.omg.CosNaming.NamingContextPackage.InvalidName e1) {
			log.severe("Remote server closed connection: Invalid name!");
		} catch (Exception e) {
			log.warning("Warning: "+e.getMessage());
		}
	}
}
