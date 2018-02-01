package com.sysgears.filesplitter.splitter.validator;

import com.sysgears.filesplitter.splitter.InvalidCommandException;
import com.sysgears.filesplitter.splitter.SizeUnits;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;

public class ITestSplitCommandValidator {

    private final String resourcePath = System.getProperty("user.dir") + "/src/test/resources/files";

    private SplitCommandValidator splitCommandValidator;

    @BeforeClass
    public void setUp() {
        Logger logger = Logger.getRootLogger();
        splitCommandValidator = new SplitCommandValidatorImpl(logger);
    }

    @AfterMethod
    public void clearResources() throws IOException {
        File directory = new File(resourcePath);
        FileUtils.cleanDirectory(directory);
    }

    @Test(expectedExceptions = InvalidCommandException.class)
    public void testCheckFileExistenceIfFileNotExist() throws InvalidCommandException {
        //Arrange
        String nonExistFilePath = resourcePath + "/nonexistent.avi";
        String[] command = {"split", "-p", nonExistFilePath, "-s", "10M"};

        //Act
        splitCommandValidator.checkFileExistence(command);
    }

    @Test
    public void testCheckFileExistenceIfFileExists() throws InvalidCommandException, IOException {
        //Arrange
        String existingFilePath = resourcePath + "/existing.avi";
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(existingFilePath, "rw")) {
            randomAccessFile.setLength(1000);
        }
        String[] command = {"split", "-p", existingFilePath, "-s", "10M"};

        //Act
        splitCommandValidator.checkFileExistence(command);
    }

    @Test(expectedExceptions = InvalidCommandException.class)
    public void testCheckEmptyFileIfFileIsEmpty() throws InvalidCommandException, IOException {
        //Arrange
        String emptyFilePath = resourcePath + "/empty.avi";
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(emptyFilePath, "rw")) {
            randomAccessFile.setLength(0);
        }
        String[] command = {"split", "-p", emptyFilePath, "-s", "10M"};

        //Act
        splitCommandValidator.checkEmptyFile(command);
    }

    @Test
    public void testCheckEmptyFileIfFileIsNotEmpty() throws InvalidCommandException, IOException {
        //Arrange
        String nonemptyFilePath = resourcePath + "/nonempty.avi";
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(nonemptyFilePath, "rw")) {
            randomAccessFile.setLength(100);
        }
        String[] command = {"split", "-p", nonemptyFilePath, "-s", "10M"};

        //Act
        splitCommandValidator.checkEmptyFile(command);
    }

    @Test(expectedExceptions = InvalidCommandException.class)
    public void testCheckCorrectPartSizeIfUnitIsUnsupported() throws InvalidCommandException {
        //Arrange
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
    }

    @Test(expectedExceptions = InvalidCommandException.class)
    public void testCheckCorrectPartSizeIfPartSizeMoreThanTotal() throws InvalidCommandException, IOException {
        //Arrange
        File file = new File(resourcePath + "/original.avi");
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw")) {
            randomAccessFile.setLength(1000);
        }
        String partSize = "1500B";
        String[] command = {"split", "-p", file.getPath(), "-s", partSize};

        //Act
        splitCommandValidator.checkCorrectPartSize(command);
    }

//split -p /home/konstantinmusienko/internship/SplMerge/myVideo.avi -s 10M
}
