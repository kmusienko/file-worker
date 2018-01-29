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
    private static final Logger logger = Logger.getLogger("transfer-logs");
    private String command;

    public Transfer(File fromFile, long fromFileOffset, long length, File toFile, long toFileOffset,
                    PropertiesProvider propertiesProvider, TaskTracker taskTracker, String command) {
        this.fromFile = fromFile;
        this.fromFileOffset = fromFileOffset;
        this.length = length;
        this.toFile = toFile;
        this.toFileOffset = toFileOffset;
        this.propertiesProvider = propertiesProvider;
        this.taskTracker = taskTracker;
        this.command = command;
    }

    @Override
    public void run() {
        String userCommand = "User command: " + command;
        String threadName = Thread.currentThread().getName();
        try {
            //      sleep(200);
            logger.trace("Creating randomAccessFile object for reading." + userCommand);
            RandomAccessFile randomAccessFromFile = new RandomAccessFile(fromFile, "r");
            logger.trace("Creating RandomAccessFile object for writing.");
            RandomAccessFile randomAccessToFile = new RandomAccessFile(toFile, "rw");

            logger.trace("Setting the file-pointer offset: " + fromFileOffset + " (randomAccessFromFile)." + userCommand);
            randomAccessFromFile.seek(fromFileOffset);
            logger.trace("Setting the file-pointer offset: " + toFileOffset + " (toFileOffset)." + userCommand);
            randomAccessToFile.seek(toFileOffset);
            final int bufferSize = propertiesProvider.BUFFER_SIZE;
            logger.trace("Buffer size: " + bufferSize + ". " + userCommand);
            long needToRead = length;
            logger.trace("NeedToRead: " + needToRead + ". " + userCommand);
            long alreadyRead = 0;
            logger.trace("AlreadyRead: " + alreadyRead + ". " + userCommand);
            while (randomAccessFromFile.getFilePointer() - fromFileOffset < length) {
                if (bufferSize >= needToRead) {
                    logger.trace("bufferSize >= needToRead." + userCommand);
                    byte[] buffer = new byte[(int) needToRead];
                    long startTime = System.nanoTime();
                    logger.trace("Start time: " + startTime + ". " + userCommand);
                    logger.trace("Reading data." + userCommand);
                    randomAccessFromFile.read(buffer);
                    logger.trace("Writing data." + userCommand);
                    randomAccessToFile.write(buffer);
                    long endTime = System.nanoTime();
                    logger.trace("End time: " + endTime + ". " + userCommand);
                    taskTracker.addCompletedTasks(needToRead);
                    logger.trace("Added completed tasks: " + needToRead + ". " + userCommand);
                    alreadyRead = alreadyRead + needToRead;
                    logger.trace("Already read: " + alreadyRead + ". " + userCommand);
                    TaskReport taskReport = new TaskReport(alreadyRead, length);
                    taskTracker.addReportPerSection(threadName, taskReport);
                    logger.trace("Added report per section. Thread: " + threadName + ", taskReport: " + taskReport
                                + ". " + userCommand);
                    taskTracker.setBufferTasks(needToRead);
                    logger.trace("Set bufferTasks: " + needToRead);
                    taskTracker.setBufferTime(endTime - startTime);
                    logger.trace("Set buffer time: " + (endTime - startTime) + ". " + userCommand);
                    // needToRead = 0;
                } else {
                    byte[] buffer = new byte[bufferSize];
                    long startTime = System.nanoTime();
                    logger.trace("Start time: " + startTime + ". " + userCommand);
                    logger.trace("Reading data." + userCommand);
                    randomAccessFromFile.read(buffer);
                    logger.trace("Writing data." + userCommand);
                    randomAccessToFile.write(buffer);
                    long endTime = System.nanoTime();
                    logger.trace("End time: " + endTime + ". " + userCommand);
                    needToRead = needToRead - bufferSize;
                    taskTracker.addCompletedTasks(bufferSize);
                    alreadyRead = alreadyRead + bufferSize;
                    taskTracker.addReportPerSection(Thread.currentThread().getName(),
                                                    new TaskReport(alreadyRead, length));
                    taskTracker.setBufferTasks(bufferSize);
                    taskTracker.setBufferTime(endTime - startTime);
                }
            }
            randomAccessFromFile.close();
            randomAccessToFile.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
//        catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }


}
