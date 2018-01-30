package com.sysgears.filesplitter;

import org.apache.log4j.Logger;

public class FileSplitterApplication {

    private final static Logger logger = Logger.getRootLogger();
    private final static Logger errorLogger = Logger.getLogger("error-file");
    private final static Logger fatalLogger = Logger.getLogger("fatal-file");

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
