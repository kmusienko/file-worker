package com.sysgears.filesplitter.splitter;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public interface FileService {

    void split(final String[] args) throws ExecutionException, InterruptedException, InvalidCommandException;

    void merge(final String[] args) throws IOException, ExecutionException, InterruptedException,
            InvalidCommandException;
}
