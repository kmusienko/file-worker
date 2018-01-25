package com.sysgears.filesplitter.splitting.parser;

import com.sysgears.filesplitter.splitting.provider.PropertiesProvider;

public class SplitParamParser {

    private PropertiesProvider propertiesProvider;

    public SplitParamParser(PropertiesProvider propertiesProvider) {
        this.propertiesProvider = propertiesProvider;
    }

    public String parsePath(String[] args) {
        return args[2];
    }

    public int parseSize(String[] args) {
        String sizeStr = args[4];
        int size;
        if (sizeStr.endsWith(propertiesProvider.BYTE)) {
            size = Integer.parseInt(sizeStr.substring(0, sizeStr.indexOf(propertiesProvider.BYTE)));
        } else if (sizeStr.endsWith(propertiesProvider.KILOBYTE)) {
            size = Integer.parseInt(sizeStr.substring(0, sizeStr.indexOf(propertiesProvider.KILOBYTE))) * 1000;
        } else if (sizeStr.endsWith(propertiesProvider.MEGABYTE)) {
            size = Integer.parseInt(sizeStr.substring(0, sizeStr.indexOf(propertiesProvider.MEGABYTE))) * 1000 * 1000;
        } else {
            throw new IllegalArgumentException("Wrong argument(file size)");
        }

        return size;
    }


}
