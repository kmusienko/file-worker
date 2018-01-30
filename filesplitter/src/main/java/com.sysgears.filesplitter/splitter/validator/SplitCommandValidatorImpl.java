package com.sysgears.filesplitter.splitter.validator;

import com.sysgears.filesplitter.splitter.InvalidCommandException;
import com.sysgears.filesplitter.splitter.SizeUnits;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Arrays;

public class SplitCommandValidatorImpl implements SplitCommandValidator {

    private Logger logger;

    public SplitCommandValidatorImpl(final Logger logger) {
        this.logger = logger;
    }

    @Override
    public void checkCommandValidity(final String[] command) throws InvalidCommandException {
        checkCommandSignature(command);
        checkFileExistence(command);
        checkEmptyFile(command);
        checkCorrectPartSize(command);
    }

    @Override
    public void checkCommandSignature(final String[] command) throws InvalidCommandException {
        logger.debug("Checking command signature.\nUser command: " + Arrays.toString(command));
        if (command.length != 5 || !command[1].equals("-p") || !command[3].equals("-s")) {
      //      logger.debug("Command signature is invalid. Will be thrown InvalidCommandException.");
            throw new InvalidCommandException("Invalid command signature.");
        }
    }

    @Override
    public void checkFileExistence(final String[] command) throws InvalidCommandException {
        logger.debug("Checking file existence.\nUser command: " + Arrays.toString(command));
        File file = new File(command[2]);
        if (!file.exists()) {
      //      logger.debug("Specified file doesn't exists. Will be thrown InvalidCommandException.");
            throw new InvalidCommandException("Specified file doesn't exists.");
        }
    }

    @Override
    public void checkEmptyFile(final String[] command) throws InvalidCommandException {
        logger.debug("Checking if file is empty.\nUser command: " + Arrays.toString(command));
        File file = new File(command[2]);
        if (file.length() == 0) {
       //     logger.debug("Specified file has zero length. Will be thrown InvalidCommandException.");
            throw new InvalidCommandException("Specified file is empty.");
        }
    }

    @Override
    public void checkCorrectPartSize(final String[] command) throws InvalidCommandException {
        logger.debug("Checking correct part size.\nUser command: " + Arrays.toString(command));
        File fileToSplit = new File((command[2]));
        String sizeStr = command[4];
 //       logger.debug("Entered part size: " + sizeStr + " User command: " + Arrays.toString(command));
        boolean isCorrectUnit = false;
        SizeUnits specifiedUnit = null;
        for (SizeUnits sizeUnit : SizeUnits.values()) {
            if (sizeStr.endsWith(String.valueOf(sizeUnit))) {
                isCorrectUnit = true;
                specifiedUnit = sizeUnit;
            }
        }
        if (!isCorrectUnit) {
       //     logger.debug("Unsupported size unit. Will be thrown InvalidCommandException.");
            throw new InvalidCommandException("Incorrect part size unit.");
        }

        long partSize = Long.parseLong(sizeStr.substring(0, sizeStr.indexOf(String.valueOf(specifiedUnit))))
                * specifiedUnit.getCoefficient();
        long fileSize = fileToSplit.length();
   //     logger.debug("Size of source file: " + fileSize + " bytes. User command: " + Arrays.toString(command));
        if (partSize > fileSize || partSize <= 0) {
        //    logger.debug("Part size is incorrect. Will be thrown InvalidCommandException.");
            throw new InvalidCommandException("Incorrect part size.");
        }
    }

}
