package com.sysgears.filesplitter.splitting.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

public class MergeParamParser {

    public List<File> parseFiles(String[] args) throws FileNotFoundException {
        String pathDirectory = args[2];
        File directory = new File(pathDirectory);
        File[] files = directory.listFiles();
        if (files == null) {
            throw new FileNotFoundException("Empty directory");
        }
        return Arrays.asList(files);
    }
}
