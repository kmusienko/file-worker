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
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ITestFileService {

    private final String resourcePath = System.getProperty("user.dir") + "/src/test/resources";
    private Logger logger;
    private PropertiesProvider propertiesProvider;
    private ExecutorService fileWorkersPool;
    private ExecutorService statisticsPool;
    private TaskTracker taskTracker;
    private FileAssistant fileAssistant;
    private SplitParamParser splitParamParser;
    private MergeParamParser mergeParamParser;
    private CommandValidator splitCommandValidator;
    private CommandValidator mergeCommandValidator;
    private FileService fileService;

    @BeforeClass
    public void setUp() {
        logger = Logger.getRootLogger();
        propertiesProvider = new PropertiesProvider();
        fileWorkersPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        statisticsPool = Executors.newFixedThreadPool(1);
        taskTracker = new TaskTrackerImpl();
        fileAssistant = new FileAssistantImpl();
        splitParamParser = new SplitParamParser(logger);
        mergeParamParser = new MergeParamParser(logger);
        splitCommandValidator = new SplitCommandValidatorImpl(logger);
        mergeCommandValidator = new MergeCommandValidatorImpl(logger);
        fileService = new FileServiceImpl(fileAssistant, splitParamParser, mergeParamParser,
                                          propertiesProvider, fileWorkersPool, statisticsPool,
                                          taskTracker, splitCommandValidator, mergeCommandValidator,
                                          logger);
    }

    @AfterTest
    public void clearResources() throws IOException {
        File directory = new File(resourcePath);
        FileUtils.cleanDirectory(directory);
    }

    //TODO: improve this test(creating parts directory)
    @Test
    public void split() throws InterruptedException, ExecutionException, InvalidCommandException {
        //Arrange
        String filePath = resourcePath + "/myFile.avi";
        final long fileSize = 1_000_000;
        final long expectedPartSize = 500_000;
        final long expectedFilesCount = 2;
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(filePath, "rw")) {
            randomAccessFile.setLength(fileSize);
        } catch (IOException ex) {
            Assert.fail(Arrays.toString(ex.getStackTrace()));
        }
        String[] command = {"split", "-p", filePath, "-s", "500000B"};

        //Act
        List<File> actualFiles = fileService.split(command);

        //Assert
        Assert.assertEquals(actualFiles.size(), expectedFilesCount);
        for (File actualFile : actualFiles) {
            Assert.assertEquals(actualFile.length(), expectedPartSize);
        }
    }

}
