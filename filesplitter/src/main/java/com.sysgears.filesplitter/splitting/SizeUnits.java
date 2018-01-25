package com.sysgears.filesplitter.splitting;

public enum SizeUnits {

    B(1),
    K(1000),
    M(1000*1000);

    private int coefficient;

    SizeUnits(int coefficient) {
        this.coefficient = coefficient;
    }

    public int getCoefficient() {
        return coefficient;
    }
}
