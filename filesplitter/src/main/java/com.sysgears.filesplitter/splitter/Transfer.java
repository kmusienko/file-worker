package com.sysgears.filesplitter.splitter;

import com.sysgears.filesplitter.splitter.provider.PropertiesProvider;
import com.sysgears.statistics.TaskReport;
import com.sysgears.statistics.TaskTracker;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Transfer extends Thread {

    private static final Logger logger = Logger.getLogger("transfer-logs");
    private final String threadName = Thread.currentThread().getName();
    private File fromFile;
    private long fromFileOffset;
    private long length;
    private File toFile;
    private long toFileOffset;
    private PropertiesProvider propertiesProvider;
    private TaskTracker taskTracker;
    private String userCommand;

    public Transfer(File fromFile, long fromFileOffset, long length, File toFile, long toFileOffset,
                    PropertiesProvider propertiesProvider, TaskTracker taskTracker, String userCommand) {
        this.fromFile = fromFile;
        this.fromFileOffset = fromFileOffset;
        this.length = length;
        this.toFile = toFile;
        this.toFileOffset = toFileOffset;
        this.propertiesProvider = propertiesProvider;
        this.taskTracker = taskTracker;
        this.userCommand = userCommand;
    }

    @Override
    public void run() {
        logger.trace("Transfer started." + this);
        try (RandomAccessFile randomAccessFromFile = new RandomAccessFile(fromFile, "r");
             RandomAccessFile randomAccessToFile = new RandomAccessFile(toFile, "rw")) {
            randomAccessFromFile.seek(fromFileOffset);
            randomAccessToFile.seek(toFileOffset);
            final int bufferSize = propertiesProvider.BUFFER_SIZE;
            long needToRead = length;
            long alreadyRead = 0;
            while (randomAccessFromFile.getFilePointer() - fromFileOffset < length) {
                if (bufferSize >= needToRead) {
                    logger.trace("bufferSize >= needToRead. FilePointer: " + randomAccessFromFile.getFilePointer()
                                         + this);
                    long time = readAndWrite(randomAccessFromFile, randomAccessToFile, needToRead);
                    taskTracker.addCompletedTasks(needToRead);
                    alreadyRead = alreadyRead + needToRead;
                    taskTracker.addReportPerSection(threadName, new TaskReport(alreadyRead, length));
                    taskTracker.setBufferTasks(needToRead);
                    taskTracker.setBufferTime(time);
                } else {
                    logger.trace("bufferSize < needToRead. FilePointer: " + randomAccessFromFile.getFilePointer()
                                         + this);
                    long time = readAndWrite(randomAccessFromFile, randomAccessToFile, bufferSize);
                    needToRead = needToRead - bufferSize;
                    taskTracker.addCompletedTasks(bufferSize);
                    alreadyRead = alreadyRead + bufferSize;
                    taskTracker.addReportPerSection(threadName, new TaskReport(alreadyRead, length));
                    taskTracker.setBufferTasks(bufferSize);
                    taskTracker.setBufferTime(time);
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
        logger.trace("Transfer completed." + this);
    }

    private long readAndWrite(RandomAccessFile randomAccessFromFile, RandomAccessFile randomAccessToFile,
                              long bufferSize) throws IOException {
        byte[] buffer = new byte[(int) bufferSize];
        long startTime = System.nanoTime();
        logger.trace("StartTime: " + startTime + this);
        randomAccessFromFile.read(buffer);
        randomAccessToFile.write(buffer);
        long endTime = System.nanoTime();
        logger.trace("EndTime: " + endTime + this);
        return endTime - startTime;
    }

    @Override
    public String toString() {
        return "Transfer{" +
                "threadName='" + threadName + '\'' +
                ", fromFile=" + fromFile +
                ", fromFileOffset=" + fromFileOffset +
                ", length=" + length +
                ", toFile=" + toFile +
                ", toFileOffset=" + toFileOffset +
                ", userCommand='" + userCommand + '\'' +
                '}';
    }
}
