package com.sysgears.statistics;

import java.util.HashMap;
import java.util.Map;

public class StatisticsServiceImpl implements StatisticsService {

    @Override
    public int calculateProgress(long completedTasks, long allTasks) {
        double semiRes = (double) completedTasks / ((double) allTasks) * 100;
        long result = Math.round(semiRes);
        return (int) result;
    }

    @Override
    public Map<String, Integer> calculateProgressPerSection(Map<String, TaskReport> reports) {
        Map<String, Integer> unitProgress = new HashMap<>();
        reports.forEach((id, report) -> unitProgress.put(id, calculateProgress(report.getCompleted(), report.getTotal())));
        return unitProgress;
    }
}
