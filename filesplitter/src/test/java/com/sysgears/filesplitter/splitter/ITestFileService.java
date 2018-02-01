package com.sysgears.filesplitter.splitter;

import com.sysgears.filesplitter.splitter.parser.MergeParamParser;
import com.sysgears.filesplitter.splitter.parser.SplitParamParser;
import com.sysgears.filesplitter.splitter.provider.PropertiesProvider;
import com.sysgears.filesplitter.splitter.validator.CommandValidator;
import com.sysgears.filesplitter.splitter.validator.MergeCommandValidatorImpl;
import com.sysgears.filesplitter.splitter.validator.SplitCommandValidatorImpl;
import com.sysgears.statistics.TaskTracker;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ITestFileService {

    private final String resourcePath = System.getProperty("user.dir") + "/src/test/resources/files";
    private FileService fileService;

    @BeforeClass
    public void setUp() {
        Logger logger = Logger.getRootLogger();
        PropertiesProvider propertiesProvider = new PropertiesProvider();
        ExecutorService fileWorkersPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        ExecutorService statisticsPool = Executors.newFixedThreadPool(1);
        TaskTracker taskTracker = new TaskTrackerImpl();
        FileAssistant fileAssistant = new FileAssistantImpl();
        SplitParamParser splitParamParser = new SplitParamParser(logger);
        MergeParamParser mergeParamParser = new MergeParamParser(logger);
        CommandValidator splitCommandValidator = new SplitCommandValidatorImpl(logger);
        CommandValidator mergeCommandValidator = new MergeCommandValidatorImpl(logger);
        fileService = new FileServiceImpl(fileAssistant, splitParamParser, mergeParamParser,
                                          propertiesProvider, fileWorkersPool, statisticsPool,
                                          taskTracker, splitCommandValidator, mergeCommandValidator,
                                          logger);
    }

    @AfterMethod
    public void clearResources() throws IOException {
        File directory = new File(resourcePath);
        FileUtils.cleanDirectory(directory);
    }

    @Test
    public void testSplitIntoEqualParts() throws InterruptedException, ExecutionException, InvalidCommandException,
            IOException {
        //Arrange
        String filePath = resourcePath + "/myFile.avi";
        final long fileSize = 1_000_000;
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(filePath, "rw")) {
            randomAccessFile.setLength(fileSize);
        }
        String[] command = {"split", "-p", filePath, "-s", "500000B"};
        final long expectedPartSize = 500_000;
        final long expectedFilesCount = 2;

        //Act
        List<File> actualFiles = fileService.split(command);

        //Assert
        Assert.assertEquals(actualFiles.size(), expectedFilesCount);
        for (File actualFile : actualFiles) {
            Assert.assertEquals(actualFile.length(), expectedPartSize);
        }
    }

    @Test
    public void testSplitIntoNotEqualParts() throws InterruptedException, ExecutionException, InvalidCommandException,
            IOException {
        //Arrange
        String filePath = resourcePath + "/myFile.avi";
        final long fileSize = 1_000_000;
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(filePath, "rw")) {
            randomAccessFile.setLength(fileSize);
        }
        String[] command = {"split", "-p", filePath, "-s", "400K"};
        final long expectedPartSize = 400_000;
        final long expectedLastPartSize = 200_000;
        final long expectedFilesCount = 3;

        //Act
        List<File> actualFiles = fileService.split(command);

        //Assert
        Assert.assertEquals(actualFiles.size(), expectedFilesCount);
        for (int i = 0; i < actualFiles.size() - 1; i++) {
            Assert.assertEquals(actualFiles.get(i).length(), expectedPartSize);
        }
        Assert.assertEquals(actualFiles.get(actualFiles.size() - 1).length(), expectedLastPartSize);
    }

    @Test(expectedExceptions = InvalidCommandException.class)
    public void testFailToSplitIfArgumentsAreInvalid()
            throws InterruptedException, ExecutionException, InvalidCommandException, IOException {
        //Arrange
        String[] command = {"split","something"};

        //Act
        fileService.split(command);
    }

    @Test(expectedExceptions = InvalidCommandException.class)
    public void testFailToSplitIfFileNotExist()
            throws InterruptedException, ExecutionException, InvalidCommandException, IOException {
        //Arrange
        String nonExistFilePath = resourcePath + "/nonexistent.avi";
        String[] command = {"split", "-p", nonExistFilePath, "-s", "10M"};

        //Act
        fileService.split(command);
    }

    @Test
    public void testMerge() throws IOException, InterruptedException, ExecutionException, InvalidCommandException {
        //Arrange
        String directoryPath = resourcePath + "/parts";
        Files.createDirectory(Paths.get(directoryPath));
        long partSize = 200_000;
        long totalSize = 0;
        for (int i = 0; i < 5; i++) {
            try (RandomAccessFile randomAccessFile = new RandomAccessFile(directoryPath + "/" + i + ".avi", "rw")) {
                randomAccessFile.setLength(partSize);
            }
            totalSize = totalSize + partSize;
        }
        String[] command = {"merge", "-p", directoryPath};

        //Act
        File actualFile = fileService.merge(command);

        //Assert
        Assert.assertEquals(actualFile.length(), totalSize);
    }

    @Test(expectedExceptions = InvalidCommandException.class)
    public void testFailToMergeIfDirectoryIsEmpty()
            throws IOException, InterruptedException, ExecutionException, InvalidCommandException {
        //Arrange
        String directoryPath = resourcePath + "/parts";
        Files.createDirectory(Paths.get(directoryPath));
        String[] command = {"merge", "-p", directoryPath};

        //Act
        fileService.merge(command);
    }

    @Test(expectedExceptions = InvalidCommandException.class)
    public void testFailToMergeIfDirectoryNotExist()
            throws IOException, InterruptedException, ExecutionException, InvalidCommandException {
        //Arrange
        String directoryPath = resourcePath + "/parts";
        String[] command = {"merge", "-p", directoryPath};

        //Act
        fileService.merge(command);
    }
}
