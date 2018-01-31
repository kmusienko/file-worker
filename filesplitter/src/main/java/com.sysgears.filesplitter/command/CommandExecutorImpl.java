package com.sysgears.filesplitter.command;

import com.sysgears.filesplitter.splitter.FileService;
import com.sysgears.filesplitter.splitter.InvalidCommandException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class CommandExecutorImpl implements CommandExecutor {

    private Logger logger;

    private Logger errorLogger;

    private FileService fileService;

    public CommandExecutorImpl(final Logger logger, final Logger errorLogger, final FileService fileService) {
        this.logger = logger;
        this.errorLogger = errorLogger;
        this.fileService = fileService;
    }

    @Override
    public void execute(final String commandStr) throws IOException, ExecutionException, InterruptedException,
            InvalidCommandException {
        String[] args = commandStr.split(" ");
        logger.debug("Splitted command: " + Arrays.toString(args));

        switch (Commands.valueOf(args[0].toUpperCase())) {
            case SPLIT:
                logger.debug("Executing split command: " + commandStr);
                fileService.split(args);
                break;
            case MERGE:
                logger.debug("Executing merge command: " + commandStr);
                fileService.merge(args);
                break;
        }
    }
}
