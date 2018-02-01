package com.sysgears.filesplitter.splitter.validator;

import com.sysgears.filesplitter.splitter.InvalidCommandException;
import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Unit tests for MergeCommandValidator.
 */
public class UTestMergeCommandValidator {

    private MergeCommandValidator mergeCommandValidator;

    @BeforeClass
    public void setUp() {
        Logger logger = Logger.getRootLogger();
        mergeCommandValidator = new MergeCommandValidatorImpl(logger);
    }

    @Test(expectedExceptions = InvalidCommandException.class)
    public void testCheckCommandSignatureIfParametersAreWrong() throws InvalidCommandException {
        //Arrange
        String[] command = {"merge", "-way", "/home/konstantinmusienko/internship/parts"};

        //Act
        mergeCommandValidator.checkCommandSignature(command);
    }

    @Test
    public void testCheckCommandSignatureIfCommandIsCorrect() throws InvalidCommandException {
        //Arrange
        String[] command = {"merge", "-p", "/home/konstantinmusienko/internship/parts"};

        //Act
        mergeCommandValidator.checkCommandSignature(command);
    }

}
