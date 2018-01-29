package com.sysgears.filesplitter.splitting;

import com.sysgears.filesplitter.splitting.provider.PropertiesProvider;
import com.sysgears.statistics.TaskReport;
import com.sysgears.statistics.TaskTracker;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Transfer extends Thread {

    private File fromFile;
    private long fromFileOffset;
    private long length;
    private File toFile;
    private long toFileOffset;
    private PropertiesProvider propertiesProvider;
    private TaskTracker taskTracker;

    public Transfer(File fromFile, long fromFileOffset, long length, File toFile, long toFileOffset,
                    PropertiesProvider propertiesProvider, TaskTracker taskTracker) {
        this.fromFile = fromFile;
        this.fromFileOffset = fromFileOffset;
        this.length = length;
        this.toFile = toFile;
        this.toFileOffset = toFileOffset;
        this.propertiesProvider = propertiesProvider;
        this.taskTracker = taskTracker;
    }

    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();
        try {
            RandomAccessFile randomAccessFromFile = new RandomAccessFile(fromFile, "r");
            RandomAccessFile randomAccessToFile = new RandomAccessFile(toFile, "rw");
            randomAccessFromFile.seek(fromFileOffset);
            randomAccessToFile.seek(toFileOffset);
            final int bufferSize = propertiesProvider.BUFFER_SIZE;
            long needToRead = length;
            long alreadyRead = 0;
            while (randomAccessFromFile.getFilePointer() - fromFileOffset < length) {
                if (bufferSize >= needToRead) {
                    byte[] buffer = new byte[(int) needToRead];
                    long startTime = System.nanoTime();
                    randomAccessFromFile.read(buffer);
                    randomAccessToFile.write(buffer);
                    long endTime = System.nanoTime();
                    taskTracker.addCompletedTasks(needToRead);
                    alreadyRead = alreadyRead + needToRead;
                    TaskReport taskReport = new TaskReport(alreadyRead, length);
                    taskTracker.addReportPerSection(threadName, taskReport);
                    taskTracker.setBufferTasks(needToRead);
                    taskTracker.setBufferTime(endTime - startTime);
                } else {
                    byte[] buffer = new byte[bufferSize];
                    long startTime = System.nanoTime();
                    randomAccessFromFile.read(buffer);
                    randomAccessToFile.write(buffer);
                    long endTime = System.nanoTime();
                    needToRead = needToRead - bufferSize;
                    taskTracker.addCompletedTasks(bufferSize);
                    alreadyRead = alreadyRead + bufferSize;
                    taskTracker.addReportPerSection(threadName, new TaskReport(alreadyRead, length));
                    taskTracker.setBufferTasks(bufferSize);
                    taskTracker.setBufferTime(endTime - startTime);
                }
            }
            randomAccessFromFile.close();
            randomAccessToFile.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }


}
