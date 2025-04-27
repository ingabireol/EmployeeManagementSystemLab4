package com.olim.employeemanagementsystem.util;

import java.io.IOException;
import java.util.logging.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for configuring and using loggers across the application.
 * Provides centralized logging configuration and helper methods.
 */
public class LoggingUtility {
    private static final Logger LOGGER = Logger.getLogger(LoggingUtility.class.getName());
    private static Handler fileHandler;
    private static final String LOG_FILE_FORMAT = "logs/employee_management_%s.log";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    static {
        try {
            configureLogger();
        } catch (IOException e) {
            // If we can't set up file logging, we'll still log to console
            System.err.println("Could not initialize file logging: " + e.getMessage());
        }
    }
    
    /**
     * Configures the logging system with file and console handlers.
     */
    private static void configureLogger() throws IOException {
        // Get the root logger
        Logger rootLogger = Logger.getLogger("");
        
        // Remove existing handlers
        for (Handler handler : rootLogger.getHandlers()) {
            rootLogger.removeHandler(handler);
        }
        
        // Create a console handler
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.INFO);
        consoleHandler.setFormatter(new SimpleFormatter());
        rootLogger.addHandler(consoleHandler);
        
        // Create a file handler
        String logFileName = String.format(LOG_FILE_FORMAT, 
                LocalDateTime.now().format(DATE_FORMAT));
        fileHandler = new FileHandler(logFileName, true);
        fileHandler.setLevel(Level.ALL);
        fileHandler.setFormatter(new SimpleFormatter());
        rootLogger.addHandler(fileHandler);
        
        // Set the overall log level
        rootLogger.setLevel(Level.INFO);
    }
    
    /**
     * Get a logger for a specific class.
     */
    public static Logger getLogger(Class<?> clazz) {
        return Logger.getLogger(clazz.getName());
    }
    
    /**
     * Closes the file handler when the application shuts down.
     */
    public static void shutdown() {
        if (fileHandler != null) {
            fileHandler.close();
        }
    }
}