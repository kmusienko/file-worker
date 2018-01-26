package com.sysgears.filesplitter.splitting.validator;

import com.sysgears.filesplitter.splitting.InvalidCommandException;
import com.sysgears.filesplitter.splitting.SizeUnits;

import java.io.File;

public class SplitCommandValidatorImpl implements SplitCommandValidator {

    @Override
    public void checkCommandValidity(final String[] command) throws InvalidCommandException {
        checkCommandSignature(command);
        checkFileExistence(command);
        checkEmptyFile(command);
        checkCorrectPartSize(command);
    }

    @Override
    public void checkCommandSignature(final String[] command) throws InvalidCommandException {
        if (command.length != 5 || !command[1].equals("-p") || !command[3].equals("-s")) {
            throw new InvalidCommandException("Invalid command signature.");
        }
    }

    @Override
    public void checkFileExistence(final String[] command) throws InvalidCommandException {
        File file = new File(command[2]);
        if (!file.exists()) {
            throw new InvalidCommandException("Specified file doesn't exists.");
        }
    }

    @Override
    public void checkEmptyFile(final String[] command) throws InvalidCommandException {
        File file = new File(command[2]);
        if (file.length() == 0) {
            throw new InvalidCommandException("Specified file is empty.");
        }
    }

    @Override
    public void checkCorrectPartSize(final String[] command) throws InvalidCommandException {
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
        if (!isCorrectUnit) throw new InvalidCommandException("Incorrect part size unit.");

        long partSize = Long.parseLong(sizeStr.substring(0, sizeStr.indexOf(String.valueOf(specifiedUnit))))
                * specifiedUnit.getCoefficient();
        long fileSize = fileToSplit.length();
        if (partSize > fileSize || partSize <= 0) {
            throw new InvalidCommandException("Incorrect part size.");
        }
    }

}
