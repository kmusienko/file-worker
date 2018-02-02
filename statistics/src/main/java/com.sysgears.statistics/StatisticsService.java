package com.sysgears.statistics;

import java.util.Map;

/**
 * Statistics service.
 */
public interface StatisticsService {

    /**
     * Calculates progress in percentage.
     *
     * @param completedTasks number of completed tasks
     * @param allTasks       number of total tasks
     * @return progress
     */
    int calculateProgress(final long completedTasks, final long allTasks);

    /**
     * Calculates progress for each section.
     *
     * @param reports map of section and corresponding task report
     * @return progress for each section
     */
    Map<String, Integer> calculateProgressPerSection(final Map<String, TaskReport> reports);

    /**
     * Calculates time remaining.
     *
     * @param bufferTasks       buffer of bytes
     * @param bufferTimeNanoSec time to read and write buffer bytes (in nanoseconds)
     * @param remainingTasks    the number of remaining tasks
     * @return time remaining
     */
    long calculateTimeRemaining(final long bufferTasks, final long bufferTimeNanoSec, final long remainingTasks);

}
