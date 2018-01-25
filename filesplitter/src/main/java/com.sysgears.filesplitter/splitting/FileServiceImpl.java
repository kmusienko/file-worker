package com.sysgears.filesplitter.splitting;

import com.sysgears.filesplitter.splitting.parser.MergeParamParser;
import com.sysgears.filesplitter.splitting.parser.SplitParamParser;
import com.sysgears.filesplitter.splitting.provider.PropertiesProvider;

import com.sysgears.statistics.ProgressPrinter;
import com.sysgears.statistics.TaskTracker;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class FileServiceImpl implements FileService {

    private FileAssistant fileAssistant;

    private SplitParamParser splitParamParser;

    private MergeParamParser mergeParamParser;

    private PropertiesProvider propertiesProvider;

    private ExecutorService fileWorkersPool;

    private ExecutorService statisticsPool;

    private TaskTracker taskTracker;

    public FileServiceImpl(FileAssistant fileAssistant, SplitParamParser splitParamParser,
                           MergeParamParser mergeParamParser, PropertiesProvider propertiesProvider,
                           ExecutorService fileWorkersPool, ExecutorService statisticsPool, TaskTracker taskTracker) {
        this.fileAssistant = fileAssistant;
        this.splitParamParser = splitParamParser;
        this.mergeParamParser = mergeParamParser;
        this.propertiesProvider = propertiesProvider;
        this.fileWorkersPool = fileWorkersPool;
        this.taskTracker = taskTracker;
        this.statisticsPool = statisticsPool;
    }

    @Override
    public void split(String[] args) throws ExecutionException, InterruptedException {
        File file = new File(splitParamParser.parsePath(args));
        long partSize = splitParamParser.parseSize(args);
        long fileSize = file.length();
        long numSplits = fileSize / partSize;
        long remainingBytes = fileSize % partSize;
    //    if (remainingBytes != 0) numSplits = numSplits + 1;

        taskTracker.setTotalTasks(fileSize);

        List<Future<?>> futures = new ArrayList<>();
     //   long iterations = remainingBytes == 0 ? numSplits : numSplits - 1;
        for (long i = 0; i < numSplits; i++) {
            File partFile = new File(file.getParent() + "/parts/" + i + "."
                    + FilenameUtils.getExtension(file.getName()));
            Future<?> f = fileWorkersPool.submit(new Transfer(file, i * partSize, partSize, partFile, 0,
                    propertiesProvider, taskTracker));
            futures.add(f);
        }
        if (remainingBytes > 0) {
            File partFile = new File(file.getParent() + "/parts/" + (numSplits) + "."
                    + FilenameUtils.getExtension(file.getName()));
            Future<?> f = fileWorkersPool.submit(
                    new Transfer(file, fileSize - remainingBytes, remainingBytes, partFile, 0,
                            propertiesProvider, taskTracker));
            futures.add(f);
        }
        Future<?> f = statisticsPool.submit(new ProgressPrinter(taskTracker));
        futures.add(f);
        for (Future<?> future : futures) {
            future.get();
        }
        taskTracker.setTotalTasks(0);
        taskTracker.setCompletedTasks(0);
        taskTracker.getReportsPerSection().clear();

    }

    @Override
    public void merge(String[] args) throws IOException, ExecutionException, InterruptedException {
        List<File> files = mergeParamParser.parseFiles(args);
        long totalSize = fileAssistant.calculateTotalSize(files);
        taskTracker.setTotalTasks(totalSize);

        String originalFilePath = files.get(0).getParent() + "/" + propertiesProvider.SOURCE_FILENAME + "."
                + FilenameUtils.getExtension(files.get(0).getName());
        File originalFile = fileAssistant.createFile(originalFilePath, totalSize);

        files.sort(Comparator.comparingInt(o -> Integer.parseInt(FilenameUtils.getBaseName(o.getName()))));

        long iterations = files.get(files.size() - 1).length() < files.get(0).length() ? files.size() - 1 : files.size();
        List<Future<?>> futures = new ArrayList<>();
        for (int i = 0; i < iterations; i++) {
            long num = Integer.parseInt(FilenameUtils.getBaseName(files.get(i).getName()));
            Future<?> f = fileWorkersPool.submit(new Transfer(files.get(i), 0, files.get(i).length(), originalFile,
                    num * files.get(i).length(), propertiesProvider, taskTracker));
            futures.add(f);

        }
        if (iterations == files.size() - 1) {
            Future<?> f = fileWorkersPool.submit(new Transfer(files.get(files.size() - 1), 0, files.get(files.size() - 1).length(),
                    originalFile, totalSize - files.get(files.size() - 1).length(), propertiesProvider, taskTracker));
            futures.add(f);
        }
        Future<?> f = statisticsPool.submit(new ProgressPrinter(taskTracker));
        futures.add(f);
        for (Future<?> future : futures) {
            future.get();
        }
        taskTracker.setTotalTasks(0);
        taskTracker.setCompletedTasks(0);
        taskTracker.getReportsPerSection().clear();
    }
}
