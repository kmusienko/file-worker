package com.sysgears.statistics;

public class BufferSpeed {

    private long buffer;
    private long timeNanoSec;

    public long getBuffer() {
        return buffer;
    }

    public void setBuffer(final long buffer) {
        this.buffer = buffer;
    }

    public long getTimeNanoSec() {
        return timeNanoSec;
    }

    public void setTimeNanoSec(final long time) {
        this.timeNanoSec = time;
    }
}
