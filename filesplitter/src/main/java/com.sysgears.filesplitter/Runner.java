package com.sysgears.filesplitter;

import com.sysgears.filesplitter.command.CommandExecutor;
import com.sysgears.filesplitter.command.CommandExecutorImpl;
import com.sysgears.filesplitter.splitting.FileAssistant;
import com.sysgears.filesplitter.splitting.FileAssistantImpl;
import com.sysgears.filesplitter.splitting.FileService;
import com.sysgears.filesplitter.splitting.FileServiceImpl;
import com.sysgears.filesplitter.splitting.InvalidCommandException;
import com.sysgears.filesplitter.splitting.TaskTrackerImpl;
import com.sysgears.filesplitter.splitting.parser.MergeParamParser;
import com.sysgears.filesplitter.splitting.parser.SplitParamParser;
import com.sysgears.filesplitter.splitting.provider.PropertiesProvider;
import com.sysgears.filesplitter.splitting.validator.CommandValidator;
import com.sysgears.filesplitter.splitting.validator.MergeCommandValidatorImpl;
import com.sysgears.filesplitter.splitting.validator.SplitCommandValidatorImpl;
import com.sysgears.statistics.TaskTracker;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Runner {

    private Logger logger;

    private Logger errorLogger;

    private PropertiesProvider propertiesProvider = new PropertiesProvider();

    private ExecutorService fileWorkersPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private ExecutorService statisticsPool = Executors.newFixedThreadPool(1);

    private TaskTracker taskTracker = new TaskTrackerImpl();

    public Runner(final Logger logger, final Logger errorLogger) {
        this.logger = logger;
        this.errorLogger = errorLogger;
    }

    public void run() {
        FileAssistant fileAssistant = new FileAssistantImpl(logger);
        SplitParamParser splitParamParser = new SplitParamParser(logger);
        MergeParamParser mergeParamParser = new MergeParamParser(logger);
        CommandValidator splitCommandValidator = new SplitCommandValidatorImpl(logger);
        CommandValidator mergeCommandValidator = new MergeCommandValidatorImpl(logger);
        FileService fileService = new FileServiceImpl(fileAssistant, splitParamParser, mergeParamParser,
                                                      propertiesProvider, fileWorkersPool, statisticsPool,
                                                      taskTracker, splitCommandValidator, mergeCommandValidator,
                                                      logger);

        CommandExecutor commandExecutor = new CommandExecutorImpl(logger, errorLogger, fileService);
        Scanner scanner = new Scanner(System.in);
        String clientInput = "";
        while (!clientInput.equals("exit")) {
            logger.info("Waiting for user input.");
            System.out.println("Enter the command:");
            clientInput = scanner.nextLine();
            logger.debug("User input: " + clientInput);
            //     logger.debug("Executing command.");
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
