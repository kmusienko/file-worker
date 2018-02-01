package com.sysgears.statistics;

import java.util.Map;

/**
 * Tool for interaction with statistics module.
 */
public interface TaskTracker {

    /**
     * Gets completed tasks.
     *
     * @return completed tasks
     */
    long getCompletedTasks();

    /**
     * Gets total tasks.
     *
     * @return total tasks
     */
    long getTotalTasks();

    /**
     * Gets map of section and corresponding task report.
     *
     * @return map of section and corresponding task report
     */
    Map<String, TaskReport> getReportsPerSection();

    /**
     * Adds completed tasks.
     *
     * @param completedTasks completed tasks
     */
    void addCompletedTasks(final long completedTasks);

    /**
     * Sets total tasks.
     *
     * @param totalTasks total tasks
     */
    void setTotalTasks(final long totalTasks);

    /**
     * Sets completed tasks.
     *
     * @param completedTasks completed tasks
     */
    void setCompletedTasks(final long completedTasks);

    /**
     * Adds map of section and corresponding task report.
     *
     * @param name       name of the section
     * @param taskReport report about completed and total tasks
     */
    void addReportPerSection(final String name, final TaskReport taskReport);

    /**
     * Sets number of tasks in the buffer.
     *
     * @param buffer buffer of tasks
     */
    void setBufferTasks(final long buffer);

    /**
     * Sets time for buffer tasks.
     *
     * @param time time in nanoseconds
     */
    void setBufferTimeNanoSec(final long time);

    /**
     * Gets number of tasks in the buffer.
     *
     * @return number of tasks in the buffer
     */
    long getBufferTasks();

    /**
     * Gets time for buffer tasks.
     *
     * @return time in nanoseconds
     */
    long getBufferTimeNanoSec();

}
