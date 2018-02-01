package com.sysgears.filesplitter.splitter;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

/**
 * File assistant tool.
 */
public class FileAssistantImpl implements FileAssistant {

    /**
     * Creates file.
     *
     * @param filePath file path
     * @param size     file size
     * @return created file
     * @throws IOException if an I/O error occurs.
     */
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

    /**
     * Calculates total size of files.
     *
     * @param files list of files
     * @return total size
     */
    @Override
    public long calculateTotalSize(final List<File> files) {
        long totalSize = 0;
        for (File file : files) {
            totalSize = totalSize + file.length();
        }
        return totalSize;
    }
}
