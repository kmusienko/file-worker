package com.sysgears.filesplitter.splitting.validator;

import com.sysgears.filesplitter.splitting.InvalidCommandException;

public interface MergeCommandValidator extends CommandValidator {

    void checkCommandSignature(final String[] command) throws InvalidCommandException;

    void checkDirectoryExistence(final String[] command) throws InvalidCommandException;

    void checkEmptyDirectory(final String[] command) throws InvalidCommandException;


}
