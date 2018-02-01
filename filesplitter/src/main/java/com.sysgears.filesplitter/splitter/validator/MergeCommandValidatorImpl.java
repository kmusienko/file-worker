package com.sysgears.filesplitter.splitter.validator;

import com.sysgears.filesplitter.splitter.InvalidCommandException;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

/**
 * Merge command validation tool.
 */
public class MergeCommandValidatorImpl implements MergeCommandValidator {

    private Logger logger;

    public MergeCommandValidatorImpl(final Logger logger) {
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
        checkDirectoryExistence(command);
        checkEmptyDirectory(command);
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
        if (command.length != 3 || !command[1].equals("-p")) {
            //    logger.debug("Command signature is invalid. Will be thrown InvalidCommandException.");
            throw new InvalidCommandException("Invalid command signature.");
        }
    }

    /**
     * Checks if the directory exists.
     *
     * @param command splitted command
     * @throws InvalidCommandException in case of command invalidity
     */
    @Override
    public void checkDirectoryExistence(final String[] command) throws InvalidCommandException {
        logger.debug("Checking directory existence.\nUser command: " + Arrays.toString(command));
        File pathDirectory = new File(command[2]);
        if (!pathDirectory.isDirectory()) {
            //          logger.debug("Specified directory doesn't exists. Will be thrown InvalidCommandException.");
            throw new InvalidCommandException("Specified directory doesn't exist.");
        }
    }

    /**
     * Checks if the directory is empty.
     *
     * @param command splitted command
     * @throws InvalidCommandException in case of command invalidity
     */
    @Override
    public void checkEmptyDirectory(final String[] command) throws InvalidCommandException {
        logger.debug("Checking if specified directory is empty.\nUser command: " + Arrays.toString(command));
        File directory = new File(command[2]);
        if (Objects.requireNonNull(directory.listFiles()).length == 0) {
            //      logger.debug("Specified directory has zero files. Will be thrown InvalidCommandException.");
            throw new InvalidCommandException("Empty directory.");
        }
    }
}
