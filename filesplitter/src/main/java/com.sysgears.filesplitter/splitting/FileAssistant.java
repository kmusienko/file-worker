package com.sysgears.filesplitter.splitting;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface FileAssistant {

    File createFile(String filePath, long size) throws IOException;

    long calculateTotalSize(List<File> files);
}
