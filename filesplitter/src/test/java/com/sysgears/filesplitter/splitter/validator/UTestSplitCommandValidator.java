package com.sysgears.filesplitter.splitter.validator;

import com.sysgears.filesplitter.splitter.InvalidCommandException;
import com.sysgears.filesplitter.splitter.SizeUnits;
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
import java.util.Random;

public class UTestSplitCommandValidator {

    private final String resourcePath = System.getProperty("user.dir") + "/src/test/resources";

    private Logger logger;

    private SplitCommandValidator splitCommandValidator;

    @BeforeClass
    public void setUp() {
        logger = EasyMock.createMock(Logger.class);
        splitCommandValidator = new SplitCommandValidatorImpl(logger);
    }

    @AfterTest
    public void clearResources() throws IOException {
        File directory = new File(resourcePath);
        FileUtils.cleanDirectory(directory);
    }

    @Test(expectedExceptions = InvalidCommandException.class)
    public void checkCommandSignature_WrongLength_ExceptionThrown() throws InvalidCommandException {
        //Arrange
        logger.debug(EasyMock.anyString());
        EasyMock.expectLastCall().times(1);
        EasyMock.replay();
        String[] command = {"split", "-p"};

        //Act
        splitCommandValidator.checkCommandSignature(command);
        EasyMock.verify();
    }

    @Test(expectedExceptions = InvalidCommandException.class)
    public void checkCommandSignature_WrongParameters_ExceptionThrown() throws InvalidCommandException {
        //Arrange
        logger.debug(EasyMock.anyString());
        EasyMock.expectLastCall().times(1);
        EasyMock.replay();
        String[] command = {"split", "-way", "/home/konstantinmusienko/internship/myVideo.avi", "-s", "10M"};

        //Act
        splitCommandValidator.checkCommandSignature(command);
        EasyMock.verify();
    }

    @Test
    public void checkCommandSignature_ValidCommand_NothingReturn() throws InvalidCommandException {
        //Arrange
        logger.debug(EasyMock.anyString());
        EasyMock.expectLastCall().times(1);
        EasyMock.replay();
        String[] command = {"split", "-p", "/home/konstantinmusienko/internship/myVideo.avi", "-s", "10M"};

        //Act
        splitCommandValidator.checkCommandSignature(command);
        EasyMock.verify();
    }

    @Test(expectedExceptions = InvalidCommandException.class)
    public void checkFileExistence_NonexistentFile_ExceptionThrown() throws InvalidCommandException {
        //Arrange
        logger.debug(EasyMock.anyString());
        EasyMock.expectLastCall().times(1);
        EasyMock.replay();
        String nonExistFilePath = resourcePath + "/nonExists.avi";
        String[] command = {"split", "-p", nonExistFilePath, "-s", "10M"};

        //Act
        splitCommandValidator.checkFileExistence(command);
        EasyMock.verify();
    }

    @Test
    public void checkFileExistence_ExistingFile_NothingReturn() throws InvalidCommandException {
        //Arrange
        logger.debug(EasyMock.anyString());
        EasyMock.expectLastCall().times(1);
        EasyMock.replay();
        String existingFilePath = resourcePath + "/existing.avi";
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(existingFilePath, "rw")) {
            randomAccessFile.setLength(1000);
        } catch (IOException ex) {
            Assert.fail(Arrays.toString(ex.getStackTrace()));
        }
        String[] command = {"split", "-p", existingFilePath, "-s", "10M"};

        //Act
        splitCommandValidator.checkFileExistence(command);
        EasyMock.verify();
    }

    @Test(expectedExceptions = InvalidCommandException.class)
    public void checkEmptyFile_EmptyFile_ExceptionThrown() throws InvalidCommandException {
        //Arrange
        logger.debug(EasyMock.anyString());
        EasyMock.expectLastCall().times(1);
        EasyMock.replay();
        String emptyFilePath = resourcePath + "/empty.avi";
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(emptyFilePath, "rw")) {
            randomAccessFile.setLength(0);
        } catch (IOException ex) {
            Assert.fail(Arrays.toString(ex.getStackTrace()));
        }
        String[] command = {"split", "-p", emptyFilePath, "-s", "10M"};

        //Act
        splitCommandValidator.checkEmptyFile(command);
        EasyMock.verify();
    }

    @Test
    public void checkEmptyFile_NonemptyFile_NothingReturn() throws InvalidCommandException {
        //Arrange
        logger.debug(EasyMock.anyString());
        EasyMock.expectLastCall().times(1);
        EasyMock.replay();
        String nonemptyFilePath = resourcePath + "/nonempty.avi";
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(nonemptyFilePath, "rw")) {
            randomAccessFile.setLength(100);
        } catch (IOException ex) {
            Assert.fail(Arrays.toString(ex.getStackTrace()));
        }
        String[] command = {"split", "-p", nonemptyFilePath, "-s", "10M"};

        //Act
        splitCommandValidator.checkEmptyFile(command);
        EasyMock.verify();
    }

    @Test(expectedExceptions = InvalidCommandException.class)
    public void checkCorrectPartSize_UnsupportedUnit_ExceptionThrown() throws InvalidCommandException {
        //Arrange
        logger.debug(EasyMock.anyString());
        EasyMock.expectLastCall().times(1);
        EasyMock.replay();
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        String unsupportedSizeUnit = "";
        boolean isUnsupported = false;
        while (!isUnsupported) {
            unsupportedSizeUnit = String.valueOf(alphabet.charAt(random.nextInt(alphabet.length())));
            isUnsupported = true;
            for (SizeUnits sizeUnit : SizeUnits.values()) {
                if (unsupportedSizeUnit.equals(sizeUnit.toString())) {
                    isUnsupported = false;
                    break;
                }
            }
        }
        String[] command = {"split", "-p", resourcePath + "/someFile.avi", "-s", "10" + unsupportedSizeUnit};

        //Act
        splitCommandValidator.checkCorrectPartSize(command);
        EasyMock.verify();
    }

    @Test(expectedExceptions = InvalidCommandException.class)
    public void checkCorrectPartSize_PartSizeMoreThanTotal_ExceptionThrown() throws InvalidCommandException {
        //Arrange
        logger.debug(EasyMock.anyString());
        EasyMock.expectLastCall().times(1);
        EasyMock.replay();
        File file = new File(resourcePath + "/original.avi");
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw")) {
            randomAccessFile.setLength(1000);
        } catch (IOException ex) {
            Assert.fail(Arrays.toString(ex.getStackTrace()));
        }
        String partSize = "1500B";
        String[] command = {"split", "-p", file.getPath(), "-s", partSize};
        //Act
        splitCommandValidator.checkCorrectPartSize(command);
        EasyMock.verify();
    }


//split -p /home/konstantinmusienko/internship/SplMerge/myVideo.avi -s 10M
}
