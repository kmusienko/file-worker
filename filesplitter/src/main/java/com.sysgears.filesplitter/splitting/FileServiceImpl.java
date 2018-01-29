package com.sysgears.filesplitter.splitting;

import com.sysgears.filesplitter.splitting.parser.MergeParamParser;
import com.sysgears.filesplitter.splitting.parser.SplitParamParser;
import com.sysgears.filesplitter.splitting.provider.PropertiesProvider;

import com.sysgears.filesplitter.splitting.validator.CommandValidator;
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

public class FileServiceImpl implements FileService {

    private Logger logger;

    private FileAssistant fileAssistant;

    private SplitParamParser splitParamParser;

    private MergeParamParser mergeParamParser;

    private PropertiesProvider propertiesProvider;

    private ExecutorService fileWorkersPool;

    private ExecutorService statisticsPool;

    private TaskTracker taskTracker;

    private CommandValidator splitCommandValidator;

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

    @Override
    public void split(final String[] args) throws ExecutionException, InterruptedException, InvalidCommandException {
        splitCommandValidator.checkCommandValidity(args);

        File file = new File(splitParamParser.parsePath(args));
        long partSize = splitParamParser.parseSize(args);
        long fileSize = file.length();
        logger.debug("Source file size: " + fileSize + " bytes.\nUser command: " + Arrays.toString(args));
        long numSplits = fileSize / partSize;
        logger.debug("Number of splits: " + numSplits + ".\nUser command: " + Arrays.toString(args));
        long remainingBytes = fileSize % partSize;
        logger.debug("Remaining bytes: " + remainingBytes + ".\nUser command: " + Arrays.toString(args));
        taskTracker.setTotalTasks(fileSize);
        logger.debug("Set total tasks: " + fileSize + ".\nUser command: " + Arrays.toString(args));

        List<Future<?>> futures = new ArrayList<>();
        logger.info("Splitting. Submitting Transfer objects to the fileWorkersPool.\nUser command: "
                            + Arrays.toString(args));
        for (long i = 0; i < numSplits; i++) {
            File partFile = new File(file.getParent() + "/parts/" + i + "."
                                             + FilenameUtils.getExtension(file.getName()));
            Future<?> f = fileWorkersPool.submit(new Transfer(file, i * partSize, partSize, partFile, 0,
                                                              propertiesProvider, taskTracker, Arrays
                                                                      .toString(args)));
            futures.add(f);
        }
        if (remainingBytes > 0) {
            logger.debug("Remaining bytes > 0. One additional file will be added.\nUser command: "
                                 + Arrays.toString(args));
            File partFile = new File(file.getParent() + "/parts/" + (numSplits) + "."
                                             + FilenameUtils.getExtension(file.getName()));
            Future<?> f = fileWorkersPool.submit(
                    new Transfer(file, fileSize - remainingBytes, remainingBytes, partFile, 0,
                                 propertiesProvider, taskTracker, Arrays.toString(args)));
            futures.add(f);
        }

        logger.info("Executing statistics. Submitting ProgressPrinter object to the statisticsPool.\nUser command: "
                            + Arrays.toString(args));
        Future<?> f = statisticsPool.submit(new ProgressPrinter(taskTracker, Arrays.toString(args)));
        futures.add(f);
        for (Future<?> future : futures) {
            future.get();
        }
        logger.debug("Splitting completed.\nUser command: " + Arrays.toString(args));
        taskTracker.setTotalTasks(0);
        taskTracker.setCompletedTasks(0);
        taskTracker.getReportsPerSection().clear();
        logger.debug("Statistics reset.\nUser command: " + Arrays.toString(args));
    }

    @Override
    public void merge(final String[] args)
            throws IOException, ExecutionException, InterruptedException, InvalidCommandException {
        mergeCommandValidator.checkCommandValidity(args);

        List<File> files = mergeParamParser.parseFiles(args);
        logger.debug(
                "Calculating total size of files in the specified directory.\nUser command: " + Arrays.toString(args));
        long totalSize = fileAssistant.calculateTotalSize(files);
        logger.debug("Total size of files in the specified directory: " + totalSize + " User command: "
                             + Arrays.toString(args));
        taskTracker.setTotalTasks(totalSize);
        logger.debug("Set total tasks: " + totalSize + ".\nUser command: " + Arrays.toString(args));

        String originalFilePath = files.get(0).getParent() + "/" + propertiesProvider.SOURCE_FILENAME + "."
                + FilenameUtils.getExtension(files.get(0).getName());
        logger.debug("Original file path created: " + originalFilePath + ".\nUser command: " + Arrays.toString(args));
        logger.debug("Creating original file with path: " + originalFilePath + " and size: " + totalSize
                             + ".\nUser command: " + Arrays.toString(args));
        File originalFile = fileAssistant.createFile(originalFilePath, totalSize);

        logger.debug("Sorting files by name.\nUser command: " + Arrays.toString(args));
        files.sort(Comparator.comparingInt(o -> Integer.parseInt(FilenameUtils.getBaseName(o.getName()))));

        long iterations = files.get(files.size() - 1).length() < files.get(0).length() ? files.size() - 1 :
                files.size();
        List<Future<?>> futures = new ArrayList<>();
        logger.info("Merging. Submitting Transfer objects to the fileWorkersPool.\nUser command: "
                            + Arrays.toString(args));
        for (int i = 0; i < iterations; i++) {
            long num = Integer.parseInt(FilenameUtils.getBaseName(files.get(i).getName()));
            Future<?> f = fileWorkersPool.submit(new Transfer(files.get(i), 0, files.get(i).length(), originalFile,
                                                              num * files.get(i).length(), propertiesProvider,
                                                              taskTracker, Arrays.toString(args)));
            futures.add(f);
        }
        if (iterations == files.size() - 1) {
            Future<?> f = fileWorkersPool.submit(
                    new Transfer(files.get(files.size() - 1), 0, files.get(files.size() - 1).length(),
                                 originalFile, totalSize - files.get(files.size() - 1).length(), propertiesProvider,
                                 taskTracker, Arrays.toString(args)));
            futures.add(f);
        }
        logger.info("Executing statistics. Submitting ProgressPrinter object to the statisticsPool.\nUser command: "
                            + Arrays.toString(args));
        Future<?> f = statisticsPool.submit(new ProgressPrinter(taskTracker, Arrays.toString(args)));
        futures.add(f);
        for (Future<?> future : futures) {
            future.get();
        }
        logger.debug("Merging completed.\nUser command: " + Arrays.toString(args));
        taskTracker.setTotalTasks(0);
        taskTracker.setCompletedTasks(0);
        taskTracker.getReportsPerSection().clear();
        logger.debug("Statistics reset.\nUser command: " + Arrays.toString(args));
    }
}
