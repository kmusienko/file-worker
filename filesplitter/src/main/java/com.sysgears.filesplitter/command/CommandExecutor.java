package com.sysgears.filesplitter.command;

import com.sysgears.filesplitter.splitting.InvalidCommandException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public interface CommandExecutor {

    void execute(final String commandStr) throws IOException, ExecutionException, InterruptedException, InvalidCommandException;

}
