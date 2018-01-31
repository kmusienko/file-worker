package com.sysgears.filesplitter.splitter;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface FileService {

    List<File> split(final String[] args) throws ExecutionException, InterruptedException, InvalidCommandException;

    void merge(final String[] args) throws IOException, ExecutionException, InterruptedException,
            InvalidCommandException;
}
