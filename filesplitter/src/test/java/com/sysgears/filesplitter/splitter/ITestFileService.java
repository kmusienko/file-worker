package com.sysgears.filesplitter.splitter;

import com.sysgears.filesplitter.splitter.parser.MergeParamParser;
import com.sysgears.filesplitter.splitter.parser.SplitParamParser;
import com.sysgears.filesplitter.splitter.provider.PropertiesProvider;
import com.sysgears.filesplitter.splitter.validator.CommandValidator;
import com.sysgears.filesplitter.splitter.validator.MergeCommandValidatorImpl;
import com.sysgears.filesplitter.splitter.validator.SplitCommandValidatorImpl;
import com.sysgears.statistics.TaskTracker;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Integration tests for FileService.
 */
public class ITestFileService {

    private final String resourcePath = System.getProperty("user.dir") + "/src/test/resources/files";
    private SplitCommand splitCommand;
    private MergeCommand mergeCommand;

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
        splitCommand = new SplitCommand(logger, splitParamParser, propertiesProvider, fileWorkersPool,
                                                     statisticsPool, taskTracker, splitCommandValidator);
        mergeCommand = new MergeCommand(logger, fileAssistant, mergeParamParser, propertiesProvider,
                                                     fileWorkersPool, statisticsPool, taskTracker,
                                                     mergeCommandValidator);
    }

    @AfterMethod
    public void clearResources() throws IOException {
        File directory = new File(resourcePath);
        FileUtils.cleanDirectory(directory);
    }

    @Test
    public void testSplitMerge() throws IOException, InterruptedException, ExecutionException, InvalidCommandException {
        //Arrange
        String fileName = "myFile";
        String fileExtension = "avi";
        String filePath = resourcePath + "/" + fileName + "." + fileExtension;
        File fileToSplit = new File(filePath);
        final int fileSize = 1_000_000;
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(fileToSplit, "rw")) {
            byte[] randomBytes = new byte[fileSize];
            Random random = new Random();
            random.nextBytes(randomBytes);
            randomAccessFile.write(randomBytes);
        }
        String[] splitArgs = {"split", "-p", filePath, "-s", "150K"};
        final int expectedPartSize = 150_000;
        final int expectedLastPartSize = 100_000;
        final int expectedFilesCount = 7;

        //Act
        List<File> actualFiles = splitCommand.execute(splitArgs);

        //Assert
        Assert.assertEquals(actualFiles.size(), expectedFilesCount);
        for (int i = 0; i < actualFiles.size() - 1; i++) {
            Assert.assertEquals(actualFiles.get(i).length(), expectedPartSize);
            Assert.assertEquals(FilenameUtils.getExtension(actualFiles.get(i).getName()), fileExtension);
            Assert.assertEquals(Integer.parseInt(FilenameUtils.getBaseName(actualFiles.get(i).getName())), i);
        }
        Assert.assertEquals(actualFiles.get(actualFiles.size() - 1).length(), expectedLastPartSize);
        Assert.assertEquals(FilenameUtils.getExtension(actualFiles.get(actualFiles.size() - 1).getName()),
                            fileExtension);
        Assert.assertEquals(
                Integer.parseInt(FilenameUtils.getBaseName(actualFiles.get(actualFiles.size() - 1).getName())),
                actualFiles.size() - 1);

        //Arrange
        String directoryPath = actualFiles.get(0).getParent();
        String[] mergeArgs = {"merge", "-p", directoryPath};

        //Act
        File mergedFile = mergeCommand.execute(mergeArgs).get(0);

        //Assert
        Assert.assertEquals(FileUtils.contentEquals(mergedFile, fileToSplit), true);
    }

    @Test
    public void testSplitIntoEqualParts() throws InterruptedException, ExecutionException, InvalidCommandException,
            IOException {
        //Arrange
        String fileName = "myFile";
        String fileExtension = "avi";
        String filePath = resourcePath + "/" + fileName + "." + fileExtension;
        final long fileSize = 1_000_000;
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(filePath, "rw")) {
            randomAccessFile.setLength(fileSize);
        }
        String[] command = {"split", "-p", filePath, "-s", "500000B"};
        final long expectedPartSize = 500_000;
        final long expectedFilesCount = 2;

        //Act
        List<File> actualFiles = splitCommand.execute(command);

        //Assert
        Assert.assertEquals(actualFiles.size(), expectedFilesCount);
        for (int i = 0; i < actualFiles.size(); i++) {
            Assert.assertEquals(actualFiles.get(i).length(), expectedPartSize);
            Assert.assertEquals(FilenameUtils.getExtension(actualFiles.get(i).getName()), fileExtension);
            Assert.assertEquals(Integer.parseInt(FilenameUtils.getBaseName(actualFiles.get(i).getName())), i);
        }
    }

    @Test
    public void testSplitIntoNotEqualParts() throws InterruptedException, ExecutionException, InvalidCommandException,
            IOException {
        //Arrange
        String fileName = "myFile";
        String fileExtension = "avi";
        String filePath = resourcePath + "/" + fileName + "." + fileExtension;
        final long fileSize = 1_000_000;
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(filePath, "rw")) {
            randomAccessFile.setLength(fileSize);
        }
        String[] command = {"split", "-p", filePath, "-s", "400K"};
        final long expectedPartSize = 400_000;
        final long expectedLastPartSize = 200_000;
        final long expectedFilesCount = 3;

        //Act
        List<File> actualFiles = splitCommand.execute(command);

        //Assert
        Assert.assertEquals(actualFiles.size(), expectedFilesCount);
        for (int i = 0; i < actualFiles.size() - 1; i++) {
            Assert.assertEquals(actualFiles.get(i).length(), expectedPartSize);
            Assert.assertEquals(FilenameUtils.getExtension(actualFiles.get(i).getName()), fileExtension);
            Assert.assertEquals(Integer.parseInt(FilenameUtils.getBaseName(actualFiles.get(i).getName())), i);
        }
        Assert.assertEquals(actualFiles.get(actualFiles.size() - 1).length(), expectedLastPartSize);
        Assert.assertEquals(FilenameUtils.getExtension(actualFiles.get(actualFiles.size() - 1).getName()),
                            fileExtension);
        Assert.assertEquals(
                Integer.parseInt(FilenameUtils.getBaseName(actualFiles.get(actualFiles.size() - 1).getName())),
                actualFiles.size() - 1);
    }

    @Test(expectedExceptions = InvalidCommandException.class)
    public void testFailToSplitIfArgumentsAreInvalid()
            throws InterruptedException, ExecutionException, InvalidCommandException, IOException {
        //Arrange
        String[] command = {"split", "something"};

        //Act
        splitCommand.execute(command);
    }

    @Test(expectedExceptions = InvalidCommandException.class)
    public void testFailToSplitIfFileNotExist()
            throws InterruptedException, ExecutionException, InvalidCommandException, IOException {
        //Arrange
        String nonExistFilePath = resourcePath + "/nonexistent.avi";
        String[] command = {"split", "-p", nonExistFilePath, "-s", "10M"};

        //Act
        splitCommand.execute(command);
    }

    @Test
    public void testMerge() throws IOException, InterruptedException, ExecutionException, InvalidCommandException {
        //Arrange
        String directoryPath = resourcePath + "/parts";
        Files.createDirectory(Paths.get(directoryPath));
        String fileExtension = "avi";
        long partSize = 200_000;
        long totalSize = 0;
        for (int i = 0; i < 5; i++) {
            try (RandomAccessFile randomAccessFile = new RandomAccessFile(directoryPath + "/" + i + "."
                                                                                  + fileExtension, "rw")) {
                randomAccessFile.setLength(partSize);
            }
            totalSize = totalSize + partSize;
        }
        File expectedContent = new File(resourcePath + "/expected.avi");
        File directory = new File(directoryPath);
        List<File> files = Arrays.asList(Objects.requireNonNull(directory.listFiles()));
        files.sort(Comparator.comparingInt(o -> Integer.parseInt(FilenameUtils.getBaseName(o.getName()))));
        try (FileOutputStream fos = new FileOutputStream(expectedContent);
             BufferedOutputStream mergingStream = new BufferedOutputStream(fos)) {
            for (File file : files) {
                Files.copy(file.toPath(), mergingStream);
            }
        }
        String[] command = {"merge", "-p", directoryPath};

        //Act
        File actualFile = mergeCommand.execute(command).get(0);

        //Assert
        Assert.assertEquals(actualFile.length(), totalSize);
        Assert.assertEquals(FilenameUtils.getExtension(actualFile.getName()), fileExtension);
        Assert.assertEquals(FileUtils.contentEquals(actualFile, expectedContent), true);
    }

    @Test(expectedExceptions = InvalidCommandException.class)
    public void testFailToMergeIfDirectoryIsEmpty()
            throws IOException, InterruptedException, ExecutionException, InvalidCommandException {
        //Arrange
        String directoryPath = resourcePath + "/parts";
        Files.createDirectory(Paths.get(directoryPath));
        String[] command = {"merge", "-p", directoryPath};

        //Act
        mergeCommand.execute(command);
    }

    @Test(expectedExceptions = InvalidCommandException.class)
    public void testFailToMergeIfDirectoryNotExist()
            throws IOException, InterruptedException, ExecutionException, InvalidCommandException {
        //Arrange
        String directoryPath = resourcePath + "/parts";
        String[] command = {"merge", "-p", directoryPath};

        //Act
        mergeCommand.execute(command);
    }
}
