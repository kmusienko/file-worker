package com.sysgears.filesplitter;

import org.apache.log4j.Logger;

/**
 * Main class of the application.
 */
public class FileSplitterApplication {

    /**
     * Root logger.
     */
    private final static Logger logger = Logger.getRootLogger();

    /**
     * Error logger.
     */
    private final static Logger errorLogger = Logger.getLogger("error-file");

    /**
     * Fatal logger.
     */
    private final static Logger fatalLogger = Logger.getLogger("fatal-file");

    /**
     * Start point of the application.
     *
     * @param args arguments
     */
    public static void main(String[] args) {
        logger.info("Main method started.");
        try {
            Runner runner = new Runner(logger, errorLogger);
            logger.debug("Runner object was created.");
            runner.run();
        } catch (Throwable tr) {
            fatalLogger.fatal("Fatal error." + tr);
            System.out.println("Something went wrong.");
        }
    }
}
