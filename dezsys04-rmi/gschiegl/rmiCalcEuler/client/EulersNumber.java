package gschiegl.rmiCalcEuler.client;

import java.io.Serializable;
import java.math.BigDecimal;

import gschiegl.rmiCalcEuler.compute.Task;

/**
 * Fuehrt die eigentliche Berechnung der Annaeherung an die Eulersche Zahl durch.
 * Wird auf der Serverseite nach dem Eintreffen eines Eulersche-Zahlen-berechnen-Tasks von Aussen
 * durch die Middleware aufgerufen.
 * 
 * @author Roman Gschiegl
 */
public class EulersNumber implements Task<BigDecimal>, Serializable {

  /** Generierte Serialize-UID. */
  private static final long serialVersionUID = -5257267743902164951L;

  /** Die Anzahl an gewuenschten Nachkommastellen der Berechnung */
  private int eulerDigits = -1;
  
  /** Die Laenge der verwendeten Taylorreihe fuer die Berechnung von E */
  private int lengthTaylorPolynom = -1;
  
  /**
   * Initialisiert einen serialisierbaren Task *EulersNumber*,
   * welcher spaeter zur Berechnung der eulerschen Zahl
   * bis zur via dem Parameter *digits* angegebene Nachkommastelle
   * genutzt werden kann.
   * @param digits    Die Anzahl der gewuenschten Nachkommastellen.
   * @param lengthTaylorPolynom   Je hoeher, desto genauer wird das Ergebnis und desto laenger benoetigt die Berechnung. 
   *                              Entspricht der Anzahl an Polynomgliedern fuer die verwendete Taylorreihe.
   */
  public EulersNumber(int digits, int lengthTaylorPolynom) {
    this.eulerDigits = digits;
    this.lengthTaylorPolynom = lengthTaylorPolynom;
  }
  
  /**
   * Berechnet eine Annaeherung an die eulersche Zahl bis zur im Konstruktor 
   * angegebenen Kommastelle und gibt diese als BigDecimal zurueck.
   * 
   * Fuer die Annaeherung wird eine Potenzreihe der Form "1 + SUM[i=1; i gegen Unendlich]( 1 / i!)"
   * verwendet. Die Laenge der Potenzreihe wird im Konstruktor festgelegt.
   * Quelle: https://stackoverflow.com/questions/36751296/calculating-eulers-number-using-bigdecimal
   * 
   * @return  Die berechnete Annaeherung an die eulersche Zahl.
   */
  @Override
  public BigDecimal execute() {
    
    // in Anlehnung an das Java Tutorial werden die Teilergebnisse 
    // mit fuenf zusaetzlichen Nachkommastellen berechnet
    int scale = this.eulerDigits+5;
    
    
    // fuer jedes Glied der Potenzreihe wird dieses Zwischenergebnis durch Addition erweitert
    // der Startwert der Potenzreihe (= das erste Glied) ist EINS
    BigDecimal eulers = BigDecimal.ONE;
    
    int factorial = 1; // der aktuelle Wert von i-Fakultaet bzw. i-Faktorielle
    for(int i=1; i <= this.lengthTaylorPolynom; i++) {
      // passe aktuelle Faktorielle an, sodass naechstes Potenzreihen-Glied berechnet wird
      factorial = factorial * i;
      
      // berechne Potenzreihen-Glied "1 / i!" bzw. "1 / i-Fakultaet"
      // die BigDecimal-Feldvariablen und -Methoden werden verwendet, damit die gewuenschte
      // Anzahl an Kommastellen berechnet wird
      BigDecimal partEulers = BigDecimal.ONE.divide(new BigDecimal(factorial), scale, BigDecimal.ROUND_HALF_DOWN);
      
      eulers = eulers.add(partEulers);
    }
    
    // schneide Kommastellen ueber der gewuenschten Laenge ab und runde die letzte Stelle ab
    eulers = eulers.setScale(eulerDigits, BigDecimal.ROUND_HALF_DOWN);
    return eulers;
  }
  
  

}
