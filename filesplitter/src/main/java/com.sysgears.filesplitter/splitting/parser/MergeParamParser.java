package com.sysgears.filesplitter.splitting.parser;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

public class MergeParamParser {

    private Logger logger;

    public MergeParamParser(final Logger logger) {
        this.logger = logger;
    }

    public List<File> parseFiles(final String[] args) throws FileNotFoundException {
        logger.debug("Parsing files in the directory path. User command: " + Arrays.toString(args));
        String pathDirectory = args[2];
        File directory = new File(pathDirectory);
        File[] files = directory.listFiles();
//        if (files == null) {
//            throw new FileNotFoundException("Empty directory");
//        }
        return Arrays.asList(files);
    }
}
