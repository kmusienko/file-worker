package com.sysgears.filesplitter.splitter;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * File service.
 */
public interface FileService {

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
    List<File> split(final String[] args)
            throws ExecutionException, InterruptedException, InvalidCommandException, IOException;

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
    File merge(final String[] args)
            throws ExecutionException, InterruptedException, InvalidCommandException, IOException;
}
