package com.sysgears.filesplitter.splitter.validator;

import com.sysgears.filesplitter.splitter.InvalidCommandException;

public interface MergeCommandValidator extends CommandValidator {

    void checkCommandSignature(final String[] command) throws InvalidCommandException;

    void checkDirectoryExistence(final String[] command) throws InvalidCommandException;

    void checkEmptyDirectory(final String[] command) throws InvalidCommandException;


}
