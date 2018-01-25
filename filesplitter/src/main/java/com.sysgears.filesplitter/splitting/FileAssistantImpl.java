package com.sysgears.filesplitter.splitting;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

public class FileAssistantImpl implements FileAssistant {

    @Override
    public File createFile(String filePath, long size) throws IOException {
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
    public long calculateTotalSize(List<File> files) {
        long totalSize = 0;
        for (File file : files) {
            totalSize = totalSize + file.length();
        }

        return totalSize;
    }
}
