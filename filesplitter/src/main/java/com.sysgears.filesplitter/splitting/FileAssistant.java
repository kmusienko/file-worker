package com.sysgears.filesplitter.splitting;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface FileAssistant {

    File createFile(final String filePath, final long size) throws IOException;

    long calculateTotalSize(final List<File> files);
}
