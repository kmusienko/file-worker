package com.sysgears.statistics;

import java.util.Map;

public interface StatisticsService {

    int calculateProgress(final long completedTasks, final long allTasks);

    Map<String, Integer> calculateProgressPerSection(final Map<String, TaskReport> reports);

    long calculateTimeRemaining(final long bufferTasks, final long bufferTime, final long remainingTasks);

}
