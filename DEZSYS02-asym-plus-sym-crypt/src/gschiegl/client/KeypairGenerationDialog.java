package gschiegl.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Observable;

import gschiegl.logging.ConsoleLogger;

public class KeypairGenerationDialog extends Observable{

  
    private BufferedReader consoleBr = null;
  
    private USER_CHOICE_GENERATE uiGenerate = USER_CHOICE_GENERATE.GENERATE_UNKNOWN;
    private Path pubkeyPath = null;
    private Path privatekeyPath = null;
    
    enum USER_CHOICE_GENERATE {
        GENERATE_YES, GENERATE_NO, GENERATE_UNKNOWN
    }
    
    enum USER_CHOICE_YESNO {
        CHOICE_YES, CHOICE_NO, INVALID_CHOICE
    }
    
    public KeypairGenerationDialog() {
        this.consoleBr = new BufferedReader(new InputStreamReader(System.in));
    }
    
    /**
     * Starts the communication with the user. They get asked
     * whether they already used the generation dialog or not.
     * Reports back via the Observable class which choice the
     * user took.
     */
    public void start() {
        printHeader();
        
        boolean shouldWeGenerate = askForGenerationConfirmation();
        if(!shouldWeGenerate) {
            ConsoleLogger.writeInfo("Okay, you're fine. We'll continue ...");
            
            this.uiGenerate = USER_CHOICE_GENERATE.GENERATE_NO;
            setChanged();
            notifyObservers();
        } else {
            ConsoleLogger.writeInfo("We'll generate a keypair for you!");

            this.uiGenerate = USER_CHOICE_GENERATE.GENERATE_YES;
            setChanged();
            notifyObservers();
        }
    }
    
    public void printHeader() {
        System.out.println("--- RSA KEYPAIR GENERATION DIALOG ---");
        System.out.println("This dialog will guide you through generating a RSA keypair.");
        System.out.println("The keypair is needed to talk over a potentially unsecure network in an encrypted manner.");
    }
    
    
    public boolean askForGenerationConfirmation() {
        System.out.println("Have you already generated a keypair VIA THIS DIALOG earlier? (y/N)");
        
        String answer = readLineFromCommandLine().toLowerCase();
        if(answer.startsWith("y")) {
            return true;
        } else {
            return false;
        }        
    }

    /**
     * Asks the user for the desired location of the future key (the dialog is neutral, no key type referenced).
     * To validate the users answer, a few checks are done, depending on the file type :
     * A) a directory, and if yes, if we are allowed to write to it
     * B) a file, and if yes, if we WOULD be able to write to it
       *    However, in that case we want to ask the user AGAIN if he really wants to overwrite it.
       * C) something else or something where we don't have permission. In that case abort. 
     * @return The path the user gave us, if we are able to write to that location (at the moment of the check!!). Else null.
     */
    public Path askForFutureKeyPath() {
      String answer = readLineFromCommandLine();
      Path keypath = null;
      try { 
        keypath = FileSystems.getDefault().getPath(answer);
      } catch(InvalidPathException e) {
        ConsoleLogger.writeError("The path specified is invalid! Aborting.");
        System.exit(-1);
      }
      
      /**
       * Check if the path string the user specified is
       * A) a directory, and if yes, if we are allowed to write to it
       * B) a file, and if yes, if we WOULD be able to write to it
       *    However, in that case we want to ask the user AGAIN if he really wants to overwrite it.
       * C) something else or something where we don't have permission. In that case abort.
       */
      if(Files.isDirectory(keypath) && Files.isWritable(keypath)) {
        System.out.println("Writable");
        return keypath;
      } else if(Files.isRegularFile(keypath) && Files.isWritable(keypath)) {
        System.out.println("Is it okay to overwrite the file already present? (y/N)");
        USER_CHOICE_YESNO overwriteAnswer = readYesNoAnswerFromCommandLine();
        
        if(overwriteAnswer == USER_CHOICE_YESNO.CHOICE_NO) {
          //TODO: Recursively call this method again to give the user another chance
          ConsoleLogger.writeError("Okay then, have a nice day. Byte!");
          System.exit(-1);
        } else if(overwriteAnswer == USER_CHOICE_YESNO.INVALID_CHOICE) {
          System.exit(-1);
          ConsoleLogger.writeError("Okay then, have a nice day. Byte!");
          //TODO: Recursively call this method again to give the user another chance
        }
        
        System.out.println("Writeable");
        return keypath;
      } else {
        ConsoleLogger.writeError("Cannot write to that path!");
        //TODO: Recursively call this method again to give the user another chance
        System.exit(-1);
      }
      
      return null;
    }
    
    public void askForPubkeyPath() {
      System.out.println();
      System.out.println("Please specify a path to your future Public Key: ");
      
      this.pubkeyPath = askForFutureKeyPath();
    }
    
    public void askForPrivatekeyPath() {
      System.out.println();
      System.out.println("Please specify a path to your future Private Key: ");
      
      this.privatekeyPath = askForFutureKeyPath();
    }
    
    private String readLineFromCommandLine() {
      try {
        String answer = this.consoleBr.readLine();
        
        return answer;
      } catch(IOException e) {
        ConsoleLogger.writeError("Unable to read user input from console!");
      }
      
      return null;
    }
    
    private USER_CHOICE_YESNO readYesNoAnswerFromCommandLine() {
      String answer = readLineFromCommandLine().toLowerCase();
      
      if(answer.equals("y"))
        return USER_CHOICE_YESNO.CHOICE_YES;
      else if(answer.equals("n"))
        return USER_CHOICE_YESNO.CHOICE_NO;
      else
        return USER_CHOICE_YESNO.INVALID_CHOICE;
    }
    
    public USER_CHOICE_GENERATE getUiGenerate() {
        return uiGenerate;
    }

    public Path getPubkeyPath() {
        return this.pubkeyPath;
    }

    public Path getPrivatekeyPath() {
        return this.privatekeyPath;
    }
    
    
    
}
