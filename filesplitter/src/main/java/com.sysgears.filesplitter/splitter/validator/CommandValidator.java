package com.sysgears.filesplitter.splitter.validator;

import com.sysgears.filesplitter.splitter.InvalidCommandException;

public interface CommandValidator {

    void checkCommandValidity(final String[] command) throws InvalidCommandException;

}
