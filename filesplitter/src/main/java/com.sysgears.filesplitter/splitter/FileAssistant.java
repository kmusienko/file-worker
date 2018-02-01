package com.sysgears.filesplitter.splitter;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * File assistant tool.
 */
public interface FileAssistant {

    /**
     * Creates file.
     *
     * @param filePath file path
     * @param size     file size
     * @return created file
     * @throws IOException if an I/O error occurs
     */
    File createFile(final String filePath, final long size) throws IOException;

    /**
     * Calculates total size of files.
     *
     * @param files list of files
     * @return total size
     */
    long calculateTotalSize(final List<File> files);
}
