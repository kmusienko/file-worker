package com.sysgears.filesplitter.splitter;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class UTestFileAssistant {

    private final String resourcePath = System.getProperty("user.dir") + "/src/test/resources";

    private FileAssistant fileAssistant;

    @BeforeClass
    public void setUp() {
        fileAssistant = new FileAssistantImpl();
    }

    @AfterTest
    public void clearResources() throws IOException {
        File directory = new File(resourcePath);
        FileUtils.cleanDirectory(directory);
    }

    @Test
    public void createFile_PathAndSize_FileReturned() throws IOException {
        //Arrange
        String filePath = resourcePath + "/testVid.mp4";
        long size = 50_000;

        //Act
        File file = fileAssistant.createFile(filePath, size);

        //Assert
        Assert.assertEquals(file.getPath(), filePath);
        Assert.assertEquals(file.length(), size);
    }

    @Test
    public void createFile_NegativeSize_EmptyFileReturned() throws IOException {
        //Arrange
        String filePath = resourcePath + "/testVid.mp4";
        long size = -10_000;
        long expectedSize = 0;

        //Act
        File file = fileAssistant.createFile(filePath, size);

        //Assert
        Assert.assertEquals(file.getPath(), filePath);
        Assert.assertEquals(file.length(), expectedSize);
    }

    @Test(expectedExceptions = FileNotFoundException.class)
    public void createFile_InvalidPath_ExceptionThrown() throws IOException {
        //Arrange
        String invalidFilePath = resourcePath + "/something/testVid.mp4";
        long size = 50_000;

        //Act
        File file = fileAssistant.createFile(invalidFilePath, size);
    }

    @Test
    public void calculateTotalSize_ListOfFiles_SizeReturned() throws IOException {
        //Arrange
        List<File> files = new ArrayList<>();
        final long fileSize = 4_000;
        long expectedTotalSize = 0;
        for (int i = 0; i < 5; i++) {
            File file = new File(resourcePath + "/" + i + ".mp4");
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.setLength(fileSize);
            files.add(file);
            expectedTotalSize += fileSize;
        }

        //Act
        long actualTotalSize = fileAssistant.calculateTotalSize(files);

        //Assert
        Assert.assertEquals(actualTotalSize, expectedTotalSize);
    }

    @Test
    public void calculateTotalSize_EmptyFiles_ZeroReturned() throws IOException {
        //Arrange
        List<File> files = new ArrayList<>();
        final long fileSize = 0;
        long expectedTotalSize = 0;
        for (int i = 0; i < 5; i++) {
            File file = new File(resourcePath + "/" + i + ".mp4");
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.setLength(fileSize);
            files.add(file);
        }

        //Act
        long actualTotalSize = fileAssistant.calculateTotalSize(files);

        //Assert
        Assert.assertEquals(actualTotalSize, expectedTotalSize);

    }
}
