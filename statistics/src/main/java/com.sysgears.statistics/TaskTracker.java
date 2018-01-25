package com.sysgears.statistics;

import java.util.Map;

public interface TaskTracker {

    long getCompletedTasks();

    long getTotalTasks();

    Map<String, TaskReport> getReportsPerSection();

    void addCompletedTasks(long completedTasks);

    void setTotalTasks(long totalTasks);

    void setCompletedTasks(long completedTasks);

    void addReportPerSection(String name, TaskReport taskReport);

}
