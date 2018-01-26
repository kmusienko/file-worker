package com.sysgears.filesplitter.splitting.validator;

import com.sysgears.filesplitter.splitting.InvalidCommandException;

import java.io.File;
import java.util.Objects;

public class MergeCommandValidatorImpl implements MergeCommandValidator {

    @Override
    public void checkCommandValidity(final String[] command) throws InvalidCommandException {
        checkCommandSignature(command);
        checkDirectoryExistence(command);
        checkEmptyDirectory(command);
    }

    @Override
    public void checkCommandSignature(final String[] command) throws InvalidCommandException {
        if (command.length != 3 || !command[1].equals("-p")) {
            throw new InvalidCommandException("Invalid command signature.");
        }
    }

    @Override
    public void checkDirectoryExistence(final String[] command) throws InvalidCommandException {
        File pathDirectory = new File(command[2]);
        if (!pathDirectory.isDirectory()) throw new InvalidCommandException("Specified directory doesn't exist.");
    }

    @Override
    public void checkEmptyDirectory(final String[] command) throws InvalidCommandException {
        File directory = new File(command[2]);
        if (Objects.requireNonNull(directory.listFiles()).length == 0) {
            throw new InvalidCommandException("Empty directory.");
        }
    }
}
