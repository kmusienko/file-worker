package com.sysgears.filesplitter.splitter;

import com.sysgears.filesplitter.splitter.parser.MergeParamParser;
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
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * File service.
 */
public class FileServiceImpl implements FileService {

    /**
     * Root logger.
     */
    private Logger logger;

    /**
     * File assistant tool.
     */
    private FileAssistant fileAssistant;

    /**
     * Split params parser.
     */
    private SplitParamParser splitParamParser;

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
    private CommandValidator splitCommandValidator;

    /**
     * Command validation tool.
     */
    private CommandValidator mergeCommandValidator;

    public FileServiceImpl(FileAssistant fileAssistant, SplitParamParser splitParamParser,
                           MergeParamParser mergeParamParser, PropertiesProvider propertiesProvider,
                           ExecutorService fileWorkersPool, ExecutorService statisticsPool, TaskTracker taskTracker,
                           CommandValidator splitCommandValidator, CommandValidator mergeCommandValidator,
                           Logger logger) {
        this.fileAssistant = fileAssistant;
        this.splitParamParser = splitParamParser;
        this.mergeParamParser = mergeParamParser;
        this.propertiesProvider = propertiesProvider;
        this.fileWorkersPool = fileWorkersPool;
        this.taskTracker = taskTracker;
        this.statisticsPool = statisticsPool;
        this.splitCommandValidator = splitCommandValidator;
        this.mergeCommandValidator = mergeCommandValidator;
        this.logger = logger;
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
    public List<File> split(final String[] args) throws ExecutionException, InterruptedException,
            InvalidCommandException, IOException {
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

    /**
     * Merges files.
     *
     * @param args command arguments
     * @return merged file
     * @throws ExecutionException      if the computation threw an exception
     * @throws InterruptedException    in case of thread interrupting
     * @throws InvalidCommandException in case of command invalidity
     * @throws IOException             if an I/O error occurs
     */
    @Override
    public File merge(final String[] args)
            throws IOException, ExecutionException, InterruptedException, InvalidCommandException {
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

        return originalFile;
    }
}
