package com.sysgears.filesplitter.splitting.validator;

import com.sysgears.filesplitter.splitting.InvalidCommandException;

public interface MergeCommandValidator extends CommandValidator {

    void checkCommandSignature(String[] command) throws InvalidCommandException;

    void checkDirectoryExistence(String[] command) throws InvalidCommandException;

    void checkEmptyDirectory(String[] command) throws InvalidCommandException;


}
