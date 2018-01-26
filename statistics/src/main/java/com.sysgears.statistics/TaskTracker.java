package com.sysgears.statistics;

import java.util.Map;

public interface TaskTracker {

    long getCompletedTasks();

    long getTotalTasks();

    Map<String, TaskReport> getReportsPerSection();

    void addCompletedTasks(final long completedTasks);

    void setTotalTasks(final long totalTasks);

    void setCompletedTasks(final long completedTasks);

    void addReportPerSection(final String name, final TaskReport taskReport);

    void setBufferTasks(final long buffer);

    void setBufferTime(final long time);

    long getBufferTasks();

    long getBufferTime();

}
