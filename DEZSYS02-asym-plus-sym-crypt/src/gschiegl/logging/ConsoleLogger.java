package gschiegl.logging;

import java.util.logging.Logger;

public class ConsoleLogger {
    public static final Logger logger =
            Logger.getLogger(ConsoleLogger.class.getName());
    
    
    public static void writeError(String msg) {
        logger.severe(msg);
    }
    
    public static void writeInfo(String msg) {
        logger.info(msg);
    }
}
