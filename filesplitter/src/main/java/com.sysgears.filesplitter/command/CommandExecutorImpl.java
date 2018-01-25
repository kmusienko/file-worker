package com.sysgears.filesplitter.command;

import com.sysgears.filesplitter.splitting.FileService;
import com.sysgears.filesplitter.splitting.InvalidCommandException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class CommandExecutorImpl implements CommandExecutor {

    private FileService fileService;

    public CommandExecutorImpl(FileService fileService) {
        this.fileService = fileService;
    }

    @Override
    public void execute(final String commandStr) throws IOException, ExecutionException, InterruptedException,
            InvalidCommandException {
        String[] args = commandStr.split(" ");

        switch (Commands.valueOf(args[0].toUpperCase())) {
            case SPLIT:
                fileService.split(args);
                break;
            case MERGE:
                fileService.merge(args);
                break;
        }
    }
}
