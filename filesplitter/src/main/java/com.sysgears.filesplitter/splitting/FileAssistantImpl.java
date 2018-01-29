package com.sysgears.filesplitter.splitting;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.List;

public class FileAssistantImpl implements FileAssistant {

    private Logger logger;

    public FileAssistantImpl(final Logger logger) {
        this.logger = logger;
    }

    @Override
    public File createFile(final String filePath, final long size) throws IOException {
        File file = new File(filePath);
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        long restToRead = size;
        long bufferSize = 8 * 1024;
        while (randomAccessFile.length() < size) {
            if (restToRead <= bufferSize) {
                randomAccessFile.write(new byte[(int) restToRead]);
            } else {
                randomAccessFile.write(new byte[(int) bufferSize]);
                restToRead = restToRead - bufferSize;
            }
        }
        randomAccessFile.close();
        return file;
    }

    @Override
    public long calculateTotalSize(final List<File> files) {
//        logger.debug("Calculating total size of files.");
        long totalSize = 0;
        for (File file : files) {
            totalSize = totalSize + file.length();
        }
//        logger.debug("Total size = " + totalSize);
        return totalSize;
    }
}
