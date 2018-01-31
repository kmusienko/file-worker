package com.sysgears.filesplitter.splitter;

import com.sysgears.filesplitter.splitter.parser.MergeParamParser;
import com.sysgears.filesplitter.splitter.parser.SplitParamParser;
import com.sysgears.filesplitter.splitter.provider.PropertiesProvider;
import com.sysgears.filesplitter.splitter.validator.CommandValidator;
import com.sysgears.statistics.TaskTracker;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.easymock.EasyMock;
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

public class UTestFileService {

    private final String resourcePath = System.getProperty("user.dir") + "/src/test/resources";

    private Logger logger;

    private FileAssistant fileAssistant;

    private SplitParamParser splitParamParser;

    private MergeParamParser mergeParamParser;

    private PropertiesProvider propertiesProvider = new PropertiesProvider();

    private ExecutorService fileWorkersPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private ExecutorService statisticsPool = Executors.newFixedThreadPool(1);

    private TaskTracker taskTracker = new TaskTrackerImpl();

    private CommandValidator splitCommandValidator;

    private CommandValidator mergeCommandValidator;

    private FileService fileService;

    @BeforeClass
    public void setUp() {
        logger = EasyMock.createMock(Logger.class);
        fileAssistant = EasyMock.createMock(FileAssistant.class);
        splitParamParser = EasyMock.createMock(SplitParamParser.class);
        mergeParamParser = EasyMock.createMock(MergeParamParser.class);
        splitCommandValidator = EasyMock.createMock(CommandValidator.class);
        mergeCommandValidator = EasyMock.createMock(CommandValidator.class);
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

//    @Test
//    public void split_Command_SplittedFiles() throws InvalidCommandException, ExecutionException, InterruptedException {
//        //Arrange
//        String filePath = resourcePath + "/nonempty.avi";
//        long fileSize = 1_000_000;
//        long partSize = 50_000;
//        long expectedFilesCount = 2;
//        try (RandomAccessFile randomAccessFile = new RandomAccessFile(filePath, "rw")) {
//            randomAccessFile.setLength(fileSize);
//        } catch (IOException ex) {
//            Assert.fail(Arrays.toString(ex.getStackTrace()));
//        }
//        String[] command = {"split", "-p", filePath, "-s", "50000B"};
//        logger.debug(EasyMock.anyString());
//        EasyMock.expectLastCall().andVoid();
//        splitCommandValidator.checkCommandValidity(EasyMock.eq(command));
//        EasyMock.expectLastCall().andVoid();
//        EasyMock.expect(splitParamParser.parsePath(EasyMock.eq(command))).andReturn(filePath);
//        EasyMock.expect(splitParamParser.parseSize(EasyMock.eq(command))).andReturn(partSize);
//        EasyMock.replay(logger, splitCommandValidator, splitParamParser);
//
//        //Act
//        List<File> actualFiles = fileService.split(command);
//
//        //Assert
//        Assert.assertEquals(actualFiles.size(), expectedFilesCount);
//        for (File actualFile : actualFiles) {
//            Assert.assertEquals(actualFile.length(), partSize);
//        }
//    }

}
