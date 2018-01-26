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

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Runner {

    private final static Logger logger = Logger.getLogger("error-file");

    private FileAssistant fileAssistant = new FileAssistantImpl();

    private PropertiesProvider propertiesProvider = new PropertiesProvider();

    private SplitParamParser splitParamParser = new SplitParamParser();

    private MergeParamParser mergeParamParser = new MergeParamParser();

    private ExecutorService fileWorkersPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private ExecutorService statisticsPool = Executors.newFixedThreadPool(1);

    private TaskTracker taskTracker = new TaskTrackerImpl();

    private CommandValidator splitCommandValidator = new SplitCommandValidatorImpl();

    private CommandValidator mergeCommandValidator = new MergeCommandValidatorImpl();

    private FileService fileService = new FileServiceImpl(fileAssistant, splitParamParser, mergeParamParser,
                                                          propertiesProvider, fileWorkersPool, statisticsPool,
                                                          taskTracker, splitCommandValidator, mergeCommandValidator);

    public void run() {
        CommandExecutor commandExecutor = new CommandExecutorImpl(fileService);
        Scanner scanner = new Scanner(System.in);
        String clientInput = "";
        while (!clientInput.equals("exit")) {
            System.out.println("Enter the command:");
            clientInput = scanner.nextLine();
            if (!clientInput.equals("exit")) {
                try {
                    commandExecutor.execute(clientInput);
                } catch (InvalidCommandException ex) {
                    logger.error("Catched exception.", ex);
                    System.out.println(ex.getMessage());
                } catch (Exception ex) {
                    System.out.println("Bad command.");
                }
            }

        }
        fileWorkersPool.shutdown();
        statisticsPool.shutdown();

    }
}
