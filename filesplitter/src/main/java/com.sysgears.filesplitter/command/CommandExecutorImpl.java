package com.sysgears.filesplitter.command;

import com.sysgears.filesplitter.splitting.FileService;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class CommandExecutorImpl implements CommandExecutor {

    private FileService fileService;

    public CommandExecutorImpl(FileService fileService) {
        this.fileService = fileService;
    }

    @Override
    public void execute(final String commandStr) throws IOException, ExecutionException, InterruptedException {

        String[] args = commandStr.split(" ");

        if (args[0].equals(Commands.SPLIT.getName())) {
            fileService.split(args);

        } else if (args[0].equals(Commands.MERGE.getName())) {
            fileService.merge(args);
        }
    }
}
