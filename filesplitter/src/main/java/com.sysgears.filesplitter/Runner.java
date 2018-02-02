package com.sysgears.filesplitter;

import com.sysgears.filesplitter.command.CommandExecutor;
import com.sysgears.filesplitter.command.CommandExecutorImpl;
import com.sysgears.filesplitter.splitter.*;
import com.sysgears.filesplitter.splitter.parser.MergeParamParser;
import com.sysgears.filesplitter.splitter.parser.SplitParamParser;
import com.sysgears.filesplitter.splitter.provider.PropertiesProvider;
import com.sysgears.filesplitter.splitter.validator.CommandValidator;
import com.sysgears.filesplitter.splitter.validator.MergeCommandValidatorImpl;
import com.sysgears.filesplitter.splitter.validator.SplitCommandValidatorImpl;
import com.sysgears.statistics.TaskTracker;
import org.apache.log4j.Logger;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Tool for interaction with user.
 */
public class Runner {

    /**
     * Root logger.
     */
    private Logger logger;

    /**
     * Error logger.
     */
    private Logger errorLogger;

    /**
     * Tool for providing file properties.
     */
    private PropertiesProvider propertiesProvider = new PropertiesProvider();

    /**
     * File workers thread pool.
     */
    private ExecutorService fileWorkersPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    /**
     * Statistics thread pool.
     */
    private ExecutorService statisticsPool = Executors.newFixedThreadPool(1);

    /**
     * Tool for interaction with the statistics module.
     */
    private TaskTracker taskTracker = new TaskTrackerImpl();

    /**
     * Initializes loggers.
     *
     * @param logger      root logger
     * @param errorLogger error logger
     */
    public Runner(final Logger logger, final Logger errorLogger) {
        this.logger = logger;
        this.errorLogger = errorLogger;
    }

    /**
     * Interacts with user.
     */
    public void run() {
        FileAssistant fileAssistant = new FileAssistantImpl();
        SplitParamParser splitParamParser = new SplitParamParser(logger);
        MergeParamParser mergeParamParser = new MergeParamParser(logger);
        CommandValidator splitCommandValidator = new SplitCommandValidatorImpl(logger);
        CommandValidator mergeCommandValidator = new MergeCommandValidatorImpl(logger);
        FileCommand splitCommand = new SplitCommand(logger, splitParamParser, propertiesProvider, fileWorkersPool,
                                                     statisticsPool, taskTracker, splitCommandValidator);
        FileCommand mergeCommand = new MergeCommand(logger, fileAssistant, mergeParamParser, propertiesProvider,
                                                     fileWorkersPool, statisticsPool, taskTracker,
                                                     mergeCommandValidator);
        CommandExecutor commandExecutor = new CommandExecutorImpl(logger, splitCommand, mergeCommand);
        Scanner scanner = new Scanner(System.in);
        String clientInput = "";
        while (!clientInput.equals("exit")) {
            logger.info("Waiting for user input.");
            System.out.println("Enter the command:");
            clientInput = scanner.nextLine();
            logger.debug("User input: " + clientInput);
            if (!clientInput.equals("exit")) {
                try {
                    commandExecutor.execute(clientInput);
                } catch (InvalidCommandException ex) {
                    errorLogger.error("Invalid command: " + clientInput, ex);
                    System.out.println(ex.getMessage());
                } catch (Exception ex) {
                    errorLogger.error("Bad command: " + clientInput, ex);
                    System.out.println("Bad command.");
                }
            }

        }
        fileWorkersPool.shutdown();
        statisticsPool.shutdown();

    }
}
