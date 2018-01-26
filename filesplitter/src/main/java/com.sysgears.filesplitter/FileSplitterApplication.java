package com.sysgears.filesplitter;

import org.apache.log4j.Logger;

public class FileSplitterApplication {

    private final static Logger logger = Logger.getLogger(FileSplitterApplication.class);

    public static void main(String[] args) {
        logger.info("Main method started.");
        try {
            Runner runner = new Runner();
            logger.debug("Runner object was created.");
            runner.run();
        } catch (Throwable tr) {
            System.out.println("Something went wrong.");
        }
    }
}
