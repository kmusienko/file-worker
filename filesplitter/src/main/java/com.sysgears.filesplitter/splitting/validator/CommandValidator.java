package com.sysgears.filesplitter.splitting.validator;

import com.sysgears.filesplitter.splitting.InvalidCommandException;

public interface CommandValidator {

    void checkCommandValidity(final String[] command) throws InvalidCommandException;

}
