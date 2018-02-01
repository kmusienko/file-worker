package com.sysgears.filesplitter.splitter.validator;

import com.sysgears.filesplitter.splitter.InvalidCommandException;

/**
 * Split command validation tool.
 */
public interface SplitCommandValidator extends CommandValidator {

    /**
     * Checks if command signature is valid.
     *
     * @param command splitted command
     * @throws InvalidCommandException in case of command invalidity
     */
    void checkCommandSignature(final String[] command) throws InvalidCommandException;

    /**
     * Checks if the file exists.
     *
     * @param command splitted command
     * @throws InvalidCommandException in case of command invalidity
     */
    void checkFileExistence(final String[] command) throws InvalidCommandException;

    /**
     * Checks if the file is empty.
     *
     * @param command splitted command
     * @throws InvalidCommandException in case of command invalidity
     */
    void checkEmptyFile(final String[] command) throws InvalidCommandException;

    /**
     * Checks if the part size is correct.
     *
     * @param command splitted command
     * @throws InvalidCommandException in case of command invalidity
     */
    void checkCorrectPartSize(final String[] command) throws InvalidCommandException;


}
