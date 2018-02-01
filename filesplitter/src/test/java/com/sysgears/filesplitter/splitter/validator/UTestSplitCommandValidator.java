package com.sysgears.filesplitter.splitter.validator;

import com.sysgears.filesplitter.splitter.InvalidCommandException;
import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Unit tests for SplitCommandValidator.
 */
public class UTestSplitCommandValidator {

    private SplitCommandValidator splitCommandValidator;

    @BeforeClass
    public void setUp() {
        Logger logger = Logger.getRootLogger();
        splitCommandValidator = new SplitCommandValidatorImpl(logger);
    }

    @Test(expectedExceptions = InvalidCommandException.class)
    public void testCheckCommandSignatureIfLengthIsWrong() throws InvalidCommandException {
        //Arrange
        String[] command = {"split", "-p"};

        //Act
        splitCommandValidator.checkCommandSignature(command);
    }

    @Test(expectedExceptions = InvalidCommandException.class)
    public void testCheckCommandSignatureIfParametersAreWrong() throws InvalidCommandException {
        //Arrange
        String[] command = {"split", "-way", "/home/konstantinmusienko/internship/myVideo.avi", "-s", "10M"};

        //Act
        splitCommandValidator.checkCommandSignature(command);
    }

    @Test
    public void testCheckCommandSignatureIfCommandIsValid() throws InvalidCommandException {
        //Arrange
        String[] command = {"split", "-p", "/home/konstantinmusienko/internship/myVideo.avi", "-s", "10M"};

        //Act
        splitCommandValidator.checkCommandSignature(command);
    }

}
