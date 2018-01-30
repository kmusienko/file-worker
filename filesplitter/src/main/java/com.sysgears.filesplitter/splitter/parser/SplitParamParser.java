package com.sysgears.filesplitter.splitter.parser;

import com.sysgears.filesplitter.splitter.SizeUnits;
import org.apache.log4j.Logger;

import java.util.Arrays;

public class SplitParamParser {

    private Logger logger;

    public SplitParamParser(final Logger logger) {
        this.logger = logger;
    }

    public String parsePath(final String[] args) {
        logger.debug("Parsing entered file path.\nUser command: " + Arrays.toString(args));
        return args[2];
    }

    public long parseSize(final String[] args) {
        logger.debug("Parsing entered size.\nUser command: " + Arrays.toString(args));
        String sizeStr = args[4];
        long size = 0;
        for (SizeUnits sizeUnit : SizeUnits.values()) {
            if (sizeStr.endsWith(String.valueOf(sizeUnit))) {
                size = Long.parseLong(sizeStr.substring(0, sizeStr.indexOf(String.valueOf(sizeUnit))))
                        * sizeUnit.getCoefficient();
            }
        }
 //       logger.debug("Size: " + size + " bytes.");

        return size;
    }
}
