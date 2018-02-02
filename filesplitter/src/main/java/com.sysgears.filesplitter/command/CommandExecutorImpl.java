package com.sysgears.filesplitter.command;

import com.sysgears.filesplitter.splitter.FileCommand;
import com.sysgears.filesplitter.splitter.InvalidCommandException;
import com.sysgears.filesplitter.splitter.MergeCommand;
import com.sysgears.filesplitter.splitter.SplitCommand;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class CommandExecutorImpl implements CommandExecutor {

    private Logger logger;

    private FileCommand splitCommand;

    private FileCommand mergeCommand;

    public CommandExecutorImpl(Logger logger, FileCommand splitCommand,
                               FileCommand mergeCommand) {
        this.logger = logger;
        this.splitCommand = splitCommand;
        this.mergeCommand = mergeCommand;
    }

    /**
     * Executes the input command.
     *
     * @param commandStr command
     * @throws IOException             if an I/O error occurs
     * @throws ExecutionException      if the computation threw an exception
     * @throws InterruptedException    in case of thread interrupting
     * @throws InvalidCommandException in case of command invalidity
     */
    @Override
    public void execute(final String commandStr) throws IOException, ExecutionException, InterruptedException,
            InvalidCommandException {
        String[] args = commandStr.split(" ");
        logger.debug("Splitted command: " + Arrays.toString(args));

        switch (Commands.valueOf(args[0].toUpperCase())) {
            case SPLIT:
                logger.debug("Executing split command: " + commandStr);
                splitCommand.execute(args);
                break;
            case MERGE:
                logger.debug("Executing merge command: " + commandStr);
                mergeCommand.execute(args);
                break;
        }
    }
}
