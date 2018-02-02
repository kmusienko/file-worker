package com.sysgears.statistics;

/**
 * Represents buffer tasks and time to do them.
 */
public class BufferTime {

    /**
     * Buffer of tasks.
     */
    private long buffer;

    /**
     * Time to do buffer tasks (in nanoseconds).
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
