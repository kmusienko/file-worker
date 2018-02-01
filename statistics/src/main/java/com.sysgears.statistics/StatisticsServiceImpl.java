package com.sysgears.statistics;

import java.util.HashMap;
import java.util.Map;

/**
 * Statistics service.
 */
public class StatisticsServiceImpl implements StatisticsService {

    /**
     * Calculates progress in percentage.
     *
     * @param completedTasks number of completed tasks
     * @param allTasks       number of total tasks
     * @return progress
     */
    @Override
    public int calculateProgress(final long completedTasks, final long allTasks) {
        double semiRes = (double) completedTasks / ((double) allTasks) * 100;
        long result = Math.round(semiRes);
        return (int) result;
    }

    /**
     * Calculates progress for each section.
     *
     * @param reports map of section and corresponding task report
     * @return progress for each section
     */
    @Override
    public Map<String, Integer> calculateProgressPerSection(final Map<String, TaskReport> reports) {
        Map<String, Integer> unitProgress = new HashMap<>();
        reports.forEach(
                (id, report) -> unitProgress.put(id, calculateProgress(report.getCompleted(), report.getTotal())));
        return unitProgress;
    }

    /**
     * Calculates time remaining.
     *
     * @param bufferTasks       buffer of bytes
     * @param bufferTimeNanoSec time to read and write buffer bytes (in nanoseconds)
     * @param remainingTasks    remaining tasks
     * @return time remaining
     */
    @Override
    public long calculateTimeRemaining(final long bufferTasks, final long bufferTimeNanoSec,
                                       final long remainingTasks) {
        return ((remainingTasks * bufferTimeNanoSec) / bufferTasks) / 1_000_000;
    }
}
