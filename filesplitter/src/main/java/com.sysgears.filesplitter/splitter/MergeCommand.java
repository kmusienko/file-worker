package com.sysgears.filesplitter.splitter;

import com.sysgears.filesplitter.splitter.parser.MergeParamParser;
import com.sysgears.filesplitter.splitter.provider.PropertiesProvider;
import com.sysgears.filesplitter.splitter.validator.CommandValidator;
import com.sysgears.statistics.ProgressPrinter;
import com.sysgears.statistics.TaskTracker;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class MergeCommand implements FileCommand {

    /**
     * Root logger.
     */
    private Logger logger;

    /**
     * File assistant tool.
     */
    private FileAssistant fileAssistant;

    /**
     * Merge params parser.
     */
    private MergeParamParser mergeParamParser;

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
    private CommandValidator mergeCommandValidator;

    public MergeCommand(final Logger logger, final FileAssistant fileAssistant,
                        final MergeParamParser mergeParamParser,
                        final PropertiesProvider propertiesProvider, final ExecutorService fileWorkersPool,
                        final ExecutorService statisticsPool, final TaskTracker taskTracker,
                        final CommandValidator mergeCommandValidator) {
        this.logger = logger;
        this.fileAssistant = fileAssistant;
        this.mergeParamParser = mergeParamParser;
        this.propertiesProvider = propertiesProvider;
        this.fileWorkersPool = fileWorkersPool;
        this.statisticsPool = statisticsPool;
        this.taskTracker = taskTracker;
        this.mergeCommandValidator = mergeCommandValidator;
    }

    /**
     * Merges files.
     *
     * @param args command arguments
     * @return list with merged file
     * @throws ExecutionException      if the computation threw an exception
     * @throws InterruptedException    in case of thread interrupting
     * @throws InvalidCommandException in case of command invalidity
     * @throws IOException             if an I/O error occurs
     */
    @Override
    public List<File> execute(final String[] args)
            throws IOException, InvalidCommandException, ExecutionException, InterruptedException {
        mergeCommandValidator.checkCommandValidity(args);
        String userCommandStr = "\nUser command: " + Arrays.toString(args);
        List<File> files = mergeParamParser.parseFiles(args);
        long totalSize = fileAssistant.calculateTotalSize(files);
        taskTracker.setTotalTasks(totalSize);
        String originalFilePath = files.get(0).getParent() + "/" + propertiesProvider.SOURCE_FILENAME + "."
                + FilenameUtils.getExtension(files.get(0).getName());
        File originalFile = fileAssistant.createFile(originalFilePath, totalSize);
        logger.debug("Created original file with path:" + originalFilePath + " and size:" + totalSize + userCommandStr);

        files.sort(Comparator.comparingInt(o -> Integer.parseInt(FilenameUtils.getBaseName(o.getName()))));
        logger.debug("Files have been sorted by name." + userCommandStr);
        long iterations = files.get(files.size() - 1).length() < files.get(0).length() ? files.size() - 1 :
                files.size();
        List<Future<?>> futures = new ArrayList<>();
        logger.info("Merging. Submitting Transfer objects to the fileWorkersPool." + userCommandStr);
        for (int i = 0; i < iterations; i++) {
            long num = Integer.parseInt(FilenameUtils.getBaseName(files.get(i).getName()));
            Future<?> f = fileWorkersPool.submit(
                    new Transfer(files.get(i), 0, files.get(i).length(), originalFile,
                                 num * files.get(i).length(), propertiesProvider,
                                 taskTracker, userCommandStr));
            futures.add(f);
        }
        if (iterations == files.size() - 1) {
            Future<?> f = fileWorkersPool.submit(
                    new Transfer(files.get(files.size() - 1), 0, files.get(files.size() - 1).length(),
                                 originalFile, totalSize - files.get(files.size() - 1).length(),
                                 propertiesProvider, taskTracker, userCommandStr));
            futures.add(f);
        }
        logger.info("Executing statistics. Submitting ProgressPrinter object to the statisticsPool." + userCommandStr);
        Future<?> f = statisticsPool.submit(new ProgressPrinter(taskTracker, Arrays.toString(args)));
        futures.add(f);
        for (Future<?> future : futures) {
            future.get();
        }
        logger.debug("Merging completed." + userCommandStr);
        taskTracker.setTotalTasks(0);
        taskTracker.setCompletedTasks(0);
        taskTracker.getReportsPerSection().clear();
        logger.debug("Statistics reset." + userCommandStr);

        List<File> originalFiles = new ArrayList<>();
        originalFiles.add(originalFile);

        return originalFiles;
    }
}
