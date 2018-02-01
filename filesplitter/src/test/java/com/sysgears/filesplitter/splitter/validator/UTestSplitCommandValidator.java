package com.sysgears.filesplitter.splitter.validator;

import com.sysgears.filesplitter.splitter.InvalidCommandException;
import org.apache.log4j.Logger;
import org.easymock.EasyMock;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class UTestSplitCommandValidator {

    private Logger logger;

    private SplitCommandValidator splitCommandValidator;

    @BeforeClass
    public void setUp() {
        logger = EasyMock.createMock(Logger.class);
        splitCommandValidator = new SplitCommandValidatorImpl(logger);
    }

    @Test(expectedExceptions = InvalidCommandException.class)
    public void testCheckCommandSignatureIfLengthIsWrong() throws InvalidCommandException {
        //Arrange
        logger.debug(EasyMock.anyString());
        EasyMock.expectLastCall().andVoid();
        EasyMock.replay();
        String[] command = {"split", "-p"};

        //Act
        splitCommandValidator.checkCommandSignature(command);
        EasyMock.verify();
    }

    @Test(expectedExceptions = InvalidCommandException.class)
    public void testCheckCommandSignatureIfParametersAreWrong() throws InvalidCommandException {
        //Arrange
        logger.debug(EasyMock.anyString());
        EasyMock.expectLastCall().andVoid();
        EasyMock.replay();
        String[] command = {"split", "-way", "/home/konstantinmusienko/internship/myVideo.avi", "-s", "10M"};

        //Act
        splitCommandValidator.checkCommandSignature(command);
        EasyMock.verify();
    }

    @Test
    public void testCheckCommandSignatureIfCommandIsValid() throws InvalidCommandException {
        //Arrange
        logger.debug(EasyMock.anyString());
        EasyMock.expectLastCall().andVoid();
        EasyMock.replay();
        String[] command = {"split", "-p", "/home/konstantinmusienko/internship/myVideo.avi", "-s", "10M"};

        //Act
        splitCommandValidator.checkCommandSignature(command);
        EasyMock.verify();
    }

}
