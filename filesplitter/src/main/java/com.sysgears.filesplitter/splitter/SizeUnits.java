package com.sysgears.filesplitter.splitter;

/**
 * Available size units (size types)
 */
public enum SizeUnits {

    /**
     * Byte.
     */
    B(1),

    /**
     * Kilobyte.
     */
    K(1000),

    /**
     * Megabyte.
     */
    M(1000 * 1000);

    /**
     * Coefficient for converting into bytes.
     */
    private int coefficient;

    /**
     * Initializes coefficient.
     *
     * @param coefficient Coefficient for converting into bytes
     */
    SizeUnits(int coefficient) {
        this.coefficient = coefficient;
    }

    public int getCoefficient() {
        return coefficient;
    }
}
