package gschiegl.client;

import java.security.NoSuchAlgorithmException;
import java.util.Observable;
import java.util.Observer;

import gschiegl.client.KeypairGenerationDialog.USER_CHOICE_GENERATE;
import gschiegl.keys.GenerateRSAKeypair;
import gschiegl.logging.ConsoleLogger;

public class KeypairGeneration implements Observer{

    GenerateRSAKeypair keygen = null;
    KeypairGenerationDialog userDialog = null;
    
    public KeypairGeneration() {
        try {
            this.keygen = new GenerateRSAKeypair();
        } catch (NoSuchAlgorithmException e) {
            ConsoleLogger.writeError("The system does not support RSA. Aborting.");
            System.exit(-1);
        }
        
        this.userDialog = new KeypairGenerationDialog();
        this.userDialog.addObserver(this);
        this.userDialog.start();
    }

    /**
     * Process answers from interactive dialogs. For example
     * the communication of the application with the user about
     * creating the keypair is held via an external class. The answers
     * of the user are sent via an implementation of the Observer pattern.
     */
    @Override
    public void update(Observable o, Object arg) {
        
        if(o instanceof KeypairGenerationDialog) {
            /** check if Step 1 (choice generate yes/no) triggered */
            USER_CHOICE_GENERATE choiceGenerate = userDialog.getUiGenerate();
            if(choiceGenerate == USER_CHOICE_GENERATE.GENERATE_UNKNOWN) {
                userDialog.start();
            } else {
                /* continue by asking the user for a key target path */
                userDialog.askForPubkeyPath();
                userDialog.askForPrivatekeyPath();
            }
            
        }
        
    }
    
    
}
