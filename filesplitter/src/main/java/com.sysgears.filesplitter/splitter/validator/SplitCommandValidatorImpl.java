package com.sysgears.filesplitter.splitter.validator;

import com.sysgears.filesplitter.splitter.InvalidCommandException;
import com.sysgears.filesplitter.splitter.SizeUnits;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Arrays;

/**
 * Split command validation tool.
 */
public class SplitCommandValidatorImpl implements SplitCommandValidator {

    private Logger logger;

    public SplitCommandValidatorImpl(final Logger logger) {
        this.logger = logger;
    }

    /**
     * Checks if the command is valid.
     *
     * @param command input command
     * @throws InvalidCommandException in case of command invalidity
     */
    @Override
    public void checkCommandValidity(final String[] command) throws InvalidCommandException {
        checkCommandSignature(command);
        checkFileExistence(command);
        checkEmptyFile(command);
        checkCorrectPartSize(command);
    }

    /**
     * Checks if command signature is valid.
     *
     * @param command splitted command
     * @throws InvalidCommandException in case of command invalidity
     */
    @Override
    public void checkCommandSignature(final String[] command) throws InvalidCommandException {
        logger.debug("Checking command signature.\nUser command: " + Arrays.toString(command));
        if (command.length != 5 || !command[1].equals("-p") || !command[3].equals("-s")) {
            throw new InvalidCommandException("Invalid command signature.");
        }
    }

    /**
     * Checks if the file exists.
     *
     * @param command splitted command
     * @throws InvalidCommandException in case of command invalidity
     */
    @Override
    public void checkFileExistence(final String[] command) throws InvalidCommandException {
        logger.debug("Checking file existence.\nUser command: " + Arrays.toString(command));
        File file = new File(command[2]);
        if (!file.exists()) {
            throw new InvalidCommandException("Specified file doesn't exists.");
        }
    }

    /**
     * Checks if the file is empty.
     *
     * @param command splitted command
     * @throws InvalidCommandException in case of command invalidity
     */
    @Override
    public void checkEmptyFile(final String[] command) throws InvalidCommandException {
        logger.debug("Checking if file is empty.\nUser command: " + Arrays.toString(command));
        File file = new File(command[2]);
        if (file.length() == 0) {
            throw new InvalidCommandException("Specified file is empty.");
        }
    }

    /**
     * Checks if the part size is correct.
     *
     * @param command splitted command
     * @throws InvalidCommandException in case of command invalidity
     */
    @Override
    public void checkCorrectPartSize(final String[] command) throws InvalidCommandException {
        logger.debug("Checking correct part size.\nUser command: " + Arrays.toString(command));
        File fileToSplit = new File((command[2]));
        String sizeStr = command[4];
        boolean isCorrectUnit = false;
        SizeUnits specifiedUnit = null;
        for (SizeUnits sizeUnit : SizeUnits.values()) {
            if (sizeStr.endsWith(String.valueOf(sizeUnit))) {
                isCorrectUnit = true;
                specifiedUnit = sizeUnit;
            }
        }
        if (!isCorrectUnit) {
            throw new InvalidCommandException("Incorrect part size unit.");
        }

        long partSize = Long.parseLong(sizeStr.substring(0, sizeStr.indexOf(String.valueOf(specifiedUnit))))
                * specifiedUnit.getCoefficient();
        long fileSize = fileToSplit.length();
        if (partSize > fileSize || partSize <= 0) {
            throw new InvalidCommandException("Incorrect part size.");
        }
    }

}
