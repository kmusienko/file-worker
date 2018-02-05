package com.sysgears.filesplitter.splitter;

import com.sysgears.filesplitter.splitter.parser.SplitParamParser;
import com.sysgears.filesplitter.splitter.provider.PropertiesProvider;
import com.sysgears.filesplitter.splitter.validator.CommandValidator;
import com.sysgears.statistics.ProgressPrinter;
import com.sysgears.statistics.TaskTracker;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Split command.
 */
public class SplitCommand implements FileCommand {

    /**
     * Root logger.
     */
    private Logger logger;

    /**
     * Split params parser.
     */
    private SplitParamParser splitParamParser;

    /**
     * Tool for providing file properties.
     */
    private PropertiesProvider propertiesProvider;

    /**
     * File workers thread pool.
     */
    private ExecutorService fileWorkersPool;

    /**
     * Statistics thread pool.
     */
    private ExecutorService statisticsPool;

    /**
     * Interface for interaction with the statistics module.
     */
    private TaskTracker taskTracker;

    /**
     * Command validation tool.
     */
    private CommandValidator splitCommandValidator;

    public SplitCommand(final Logger logger, final SplitParamParser splitParamParser,
                        final PropertiesProvider propertiesProvider, final ExecutorService fileWorkersPool,
                        final ExecutorService statisticsPool, final TaskTracker taskTracker,
                        final CommandValidator splitCommandValidator) {
        this.logger = logger;
        this.splitParamParser = splitParamParser;
        this.propertiesProvider = propertiesProvider;
        this.fileWorkersPool = fileWorkersPool;
        this.statisticsPool = statisticsPool;
        this.taskTracker = taskTracker;
        this.splitCommandValidator = splitCommandValidator;
    }

    /**
     * Splits file.
     *
     * @param args command arguments
     * @return list of files
     * @throws ExecutionException      if the computation threw an exception
     * @throws InterruptedException    in case of thread interrupting
     * @throws InvalidCommandException in case of command invalidity
     * @throws IOException             if an I/O error occurs
     */
    @Override
    public List<File> execute(final String[] args)
            throws IOException, InvalidCommandException, ExecutionException, InterruptedException {
        splitCommandValidator.checkCommandValidity(args);
        String userCommandStr = "\nUser command: " + Arrays.toString(args);
        File file = new File(splitParamParser.parsePath(args));
        long partSize = splitParamParser.parseSize(args);
        long fileSize = file.length();
        long numSplits = fileSize / partSize;
        long remainingBytes = fileSize % partSize;
        taskTracker.setTotalTasks(fileSize);
        logger.debug("Source file size: " + fileSize + " bytes, Number of splits: " + numSplits
                             + "Remaining bytes:" + remainingBytes + userCommandStr);
        List<Future<?>> futures = new ArrayList<>();
        List<File> files = new ArrayList<>();
        logger.info("Splitting. Submitting Transfer objects to the fileWorkersPool." + userCommandStr);
        Files.createDirectory(Paths.get(file.getParent() + "/parts"));
        for (long i = 0; i < numSplits; i++) {
            File partFile = new File(file.getParent() + "/parts/" + i + "."
                                             + FilenameUtils.getExtension(file.getName()));
            Future<?> f = fileWorkersPool.submit(new Transfer(file, i * partSize, partSize, partFile, 0,
                                                              propertiesProvider, taskTracker, userCommandStr));
            futures.add(f);
            files.add(partFile);
        }
        if (remainingBytes > 0) {
            logger.debug("Remaining bytes > 0. One additional file will be added." + userCommandStr);
            File partFile = new File(file.getParent() + "/parts/" + (numSplits) + "."
                                             + FilenameUtils.getExtension(file.getName()));
            Future<?> f = fileWorkersPool.submit(
                    new Transfer(file, fileSize - remainingBytes, remainingBytes, partFile, 0,
                                 propertiesProvider, taskTracker, userCommandStr));
            futures.add(f);
            files.add(partFile);
        }
        logger.info("Executing statistics. Submitting ProgressPrinter object to the statisticsPool." + userCommandStr);
        Future<?> f = statisticsPool.submit(new ProgressPrinter(taskTracker, Arrays.toString(args)));
        futures.add(f);
        for (Future<?> future : futures) {
            future.get();
        }
        logger.debug("Splitting completed." + userCommandStr);
        taskTracker.setTotalTasks(0);
        taskTracker.setCompletedTasks(0);
        taskTracker.getReportsPerSection().clear();
        logger.debug("Statistics reset." + userCommandStr);

        return files;
    }
}
