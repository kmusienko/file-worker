package com.sysgears.statistics;

import java.util.HashMap;
import java.util.Map;

public class StatisticsServiceImpl implements StatisticsService {

    @Override
    public int calculateProgress(final long completedTasks, final long allTasks) {
        double semiRes = (double) completedTasks / ((double) allTasks) * 100;
        long result = Math.round(semiRes);
        return (int) result;
    }

    @Override
    public Map<String, Integer> calculateProgressPerSection(final Map<String, TaskReport> reports) {
        Map<String, Integer> unitProgress = new HashMap<>();
        reports.forEach(
                (id, report) -> unitProgress.put(id, calculateProgress(report.getCompleted(), report.getTotal())));
        return unitProgress;
    }

    public long calculateTimeRemaining(final long bufferTasks, final long bufferTime, final long remainingTasks) {
        return ((remainingTasks * bufferTime) / bufferTasks) / 1_000_000;
    }
}
