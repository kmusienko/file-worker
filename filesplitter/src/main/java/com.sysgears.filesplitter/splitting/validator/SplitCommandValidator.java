package com.sysgears.filesplitter.splitting.validator;

import com.sysgears.filesplitter.splitting.InvalidCommandException;

public interface SplitCommandValidator extends CommandValidator {

    void checkCommandSignature(final String[] command) throws InvalidCommandException;

    void checkFileExistence(final String[] command) throws InvalidCommandException;

    void checkEmptyFile(final String[] command) throws InvalidCommandException;

    void checkCorrectPartSize(final String[] command) throws InvalidCommandException;


}
