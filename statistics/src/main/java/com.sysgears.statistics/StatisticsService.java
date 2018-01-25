package com.sysgears.statistics;

import java.util.Map;

public interface StatisticsService {

    int calculateProgress(long completedTasks, long allTasks);

    Map<String, Integer> calculateProgressPerSection(Map<String, TaskReport> reports);

}
