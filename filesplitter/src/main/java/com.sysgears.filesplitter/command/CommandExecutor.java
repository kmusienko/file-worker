package com.sysgears.filesplitter.command;

import com.sysgears.filesplitter.splitter.InvalidCommandException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Command executing tool.
 */
public interface CommandExecutor {

    /**
     * Executes the input command.
     *
     * @param commandStr command
     * @throws IOException             if an I/O error occurs
     * @throws ExecutionException      if the computation threw an exception
     * @throws InterruptedException    in case of thread interrupting
     * @throws InvalidCommandException in case of command invalidity
     */
    void execute(final String commandStr)
            throws IOException, ExecutionException, InterruptedException, InvalidCommandException;

}
