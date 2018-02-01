package com.sysgears.filesplitter.splitter.validator;

import com.sysgears.filesplitter.splitter.InvalidCommandException;

/**
 * Merge command validation tool.
 */
public interface MergeCommandValidator extends CommandValidator {

    /**
     * Checks if command signature is valid.
     *
     * @param command splitted command
     * @throws InvalidCommandException in case of command invalidity
     */
    void checkCommandSignature(final String[] command) throws InvalidCommandException;

    /**
     * Checks if the directory exists.
     *
     * @param command splitted command
     * @throws InvalidCommandException in case of command invalidity
     */
    void checkDirectoryExistence(final String[] command) throws InvalidCommandException;

    /**
     * Checks if the directory is empty.
     *
     * @param command splitted command
     * @throws InvalidCommandException in case of command invalidity
     */
    void checkEmptyDirectory(final String[] command) throws InvalidCommandException;


}
