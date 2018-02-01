package com.sysgears.statistics;

/**
 * Represents buffer and time to read and write it.
 */
public class BufferTime {

    /**
     * Buffer of bytes.
     */
    private long buffer;

    /**
     * Time to read and write buffer bytes (in nanoseconds).
     */
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
