package com.sysgears.filesplitter.splitter;

import com.sysgears.filesplitter.splitter.provider.PropertiesProvider;
import com.sysgears.statistics.TaskReport;
import com.sysgears.statistics.TaskTracker;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Transfers a certain number of bytes from one file to another.
 */
public class Transfer extends Thread {

    /**
     * Root logger.
     */
    private static final Logger logger = Logger.getLogger("transfer-logs");

    /**
     * File from which to transfer the bytes.
     */
    private File fromFile;

    /**
     * Pointer offset in the file from which to transfer the bytes.
     */
    private long fromFileOffset;

    /**
     * Number of bytes to transfer.
     */
    private long length;

    /**
     * Transfer destination file.
     */
    private File toFile;

    /**
     * Pointer offset in the transfer destination file.
     */
    private long toFileOffset;

    /**
     * Tool for providing file properties.
     */
    private PropertiesProvider propertiesProvider;

    /**
     * Tool for interaction with the statistics module.
     */
    private TaskTracker taskTracker;

    /**
     * User command.
     */
    private String userCommand;

    /**
     * Initializes class fields.
     *
     * @param fromFile           file from which to transfer the bytes
     * @param fromFileOffset     pointer offset in the file from which to transfer the bytes
     * @param length             number of bytes to transfer
     * @param toFile             transfer destination file
     * @param toFileOffset       pointer offset in the transfer destination file
     * @param propertiesProvider tool for providing file properties
     * @param taskTracker        tool for interaction with the statistics module
     * @param userCommand        user command
     */
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

    /**
     * Executes transfer.
     */
    @Override
    public void run() {
        final String threadName = Thread.currentThread().getName();
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
                    long time = copy(randomAccessFromFile, randomAccessToFile, needToRead);
                    taskTracker.addCompletedTasks(needToRead);
                    alreadyRead = alreadyRead + needToRead;
                    taskTracker.addReportPerSection(threadName, new TaskReport(alreadyRead, length));
                    taskTracker.setBufferTasks(needToRead);
                    taskTracker.setBufferTimeNanoSec(time);
                } else {
                    logger.trace("bufferSize < needToRead. FilePointer: " + randomAccessFromFile.getFilePointer()
                                         + this);
                    long time = copy(randomAccessFromFile, randomAccessToFile, bufferSize);
                    needToRead = needToRead - bufferSize;
                    taskTracker.addCompletedTasks(bufferSize);
                    alreadyRead = alreadyRead + bufferSize;
                    taskTracker.addReportPerSection(threadName, new TaskReport(alreadyRead, length));
                    taskTracker.setBufferTasks(bufferSize);
                    taskTracker.setBufferTimeNanoSec(time);
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
        logger.trace("Transfer completed." + this);
    }

    /**
     * Reading and writing buffer bytes from one file to another.
     *
     * @param randomAccessFromFile randomAccessFile
     * @param randomAccessToFile   randomAccessFile
     * @param bufferSize           size of buffer
     * @return spent time in nanoseconds
     * @throws IOException if an I/O error occurs
     */
    private long copy(RandomAccessFile randomAccessFromFile, RandomAccessFile randomAccessToFile,
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
                "threadName='" + Thread.currentThread().getName() + '\'' +
                ", fromFile=" + fromFile +
                ", fromFileOffset=" + fromFileOffset +
                ", length=" + length +
                ", toFile=" + toFile +
                ", toFileOffset=" + toFileOffset +
                ", userCommand='" + userCommand + '\'' +
                '}';
    }
}
