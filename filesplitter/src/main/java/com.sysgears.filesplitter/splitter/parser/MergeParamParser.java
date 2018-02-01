package com.sysgears.filesplitter.splitter.parser;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

/**
 * Merge params parser.
 */
public class MergeParamParser {

    /**
     * Root logger.
     */
    private Logger logger;

    public MergeParamParser(final Logger logger) {
        this.logger = logger;
    }

    /**
     * Parses files from directory.
     *
     * @param args command arguments
     * @return parsed files
     */
    public List<File> parseFiles(final String[] args) {
        logger.debug("Parsing files in the directory path. User command: " + Arrays.toString(args));
        String pathDirectory = args[2];
        File directory = new File(pathDirectory);
        File[] files = directory.listFiles();

        return Arrays.asList(files);
    }
}
