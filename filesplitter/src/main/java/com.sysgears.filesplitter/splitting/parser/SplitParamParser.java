package com.sysgears.filesplitter.splitting.parser;

import com.sysgears.filesplitter.splitting.SizeUnits;

public class SplitParamParser {

    public String parsePath(final String[] args) {
        return args[2];
    }

    public long parseSize(final String[] args) {
        String sizeStr = args[4];
        long size = 0;
        for (SizeUnits sizeUnit : SizeUnits.values()) {
            if (sizeStr.endsWith(String.valueOf(sizeUnit))) {
                size = Long.parseLong(sizeStr.substring(0, sizeStr.indexOf(String.valueOf(sizeUnit))))
                        * sizeUnit.getCoefficient();
            }
        }

        return size;
    }
}
