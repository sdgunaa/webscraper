package util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Simple logging utility class
 */
public class Logger {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private static final int INFO = 0;
    private static final int DEBUG = 1;
    private static final int ERROR = 2;
    
    private static int logLevel = INFO; // Default to INFO level
    private final String className;
    
    /**
     * Constructor for Logger
     * 
     * @param className The name of the class using this logger
     */
    public Logger(String className) {
        this.className = className;
    }
    
    /**
     * Set the global log level
     * 
     * @param level The log level (0=INFO, 1=DEBUG, 2=ERROR)
     */
    public static void setLogLevel(int level) {
        if (level >= INFO && level <= ERROR) {
            logLevel = level;
        }
    }
    
    /**
     * Log an info message
     * 
     * @param message The message to log
     */
    public void info(String message) {
        if (logLevel <= INFO) {
            log("INFO", message);
        }
    }
    
    /**
     * Log a debug message
     * 
     * @param message The message to log
     */
    public void debug(String message) {
        if (logLevel <= DEBUG) {
            log("DEBUG", message);
        }
    }
    
    /**
     * Log an error message
     * 
     * @param message The message to log
     */
    public void error(String message) {
        if (logLevel <= ERROR) {
            log("ERROR", message);
        }
    }
    
    /**
     * Internal logging method
     * 
     * @param level The log level
     * @param message The message to log
     */
    private void log(String level, String message) {
        String timestamp = DATE_FORMAT.format(new Date());
        String threadName = Thread.currentThread().getName();
        System.out.println(String.format("[%s] [%s] [%s] [%s] %s", 
                            timestamp, threadName, level, className, message));
    }
}
