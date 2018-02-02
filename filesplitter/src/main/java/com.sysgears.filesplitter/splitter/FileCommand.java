package com.sysgears.filesplitter.splitter;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface FileCommand {

    List<File> execute(String[] args)
            throws IOException, InvalidCommandException, ExecutionException, InterruptedException;

}
