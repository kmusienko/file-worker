package com.sysgears.filesplitter.splitting;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public interface FileService {

    void split(String[] args) throws ExecutionException, InterruptedException, InvalidCommandException;

    void merge(String[] args) throws IOException, ExecutionException, InterruptedException;
}
