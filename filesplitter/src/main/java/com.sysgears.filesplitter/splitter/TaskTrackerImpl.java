package com.sysgears.filesplitter.splitter;

import com.sysgears.statistics.BufferTime;
import com.sysgears.statistics.TaskReport;
import com.sysgears.statistics.TaskTracker;

import java.util.HashMap;
import java.util.Map;

/**
 * Tool for interaction with the statistics module.
 */
public class TaskTrackerImpl implements TaskTracker {

    /**
     * Report about completed and total tasks.
     */
    private TaskReport taskReport = new TaskReport(0, 0);

    /**
     * Map of section and corresponding task report.
     */
    private Map<String, TaskReport> reportsPerSection = new HashMap<>();

    /**
     * Represents buffer and time to read and write it.
     */
    private BufferTime bufferTime = new BufferTime(0, 0);

    /**
     * Adds completed tasks.
     *
     * @param completedTasks completed tasks
     */
    @Override
    public synchronized void addCompletedTasks(final long completedTasks) {
        taskReport.addCompletedTasks(completedTasks);
    }

    /**
     * Adds map of section and corresponding task report.
     *
     * @param name       name of the section
     * @param taskReport report about completed and total tasks
     */
    @Override
    public synchronized void addReportPerSection(final String name, final TaskReport taskReport) {
        this.reportsPerSection.put(name, taskReport);
    }

    /**
     * Sets total tasks.
     *
     * @param totalTasks total tasks
     */
    @Override
    public synchronized void setTotalTasks(final long totalTasks) {
        taskReport.setTotal(totalTasks);
    }

    /**
     * Sets completed tasks.
     *
     * @param completedTasks completed tasks
     */
    @Override
    public synchronized void setCompletedTasks(final long completedTasks) {
        taskReport.setCompleted(completedTasks);
    }

    /**
     * Gets completed tasks.
     *
     * @return completed tasks
     */
    @Override
    public synchronized long getCompletedTasks() {
        return taskReport.getCompleted();
    }

    /**
     * Gets total tasks.
     *
     * @return total tasks
     */
    @Override
    public synchronized long getTotalTasks() {
        return taskReport.getTotal();
    }

    /**
     * Gets map of section and corresponding task report.
     *
     * @return map of section and corresponding task report
     */
    @Override
    public synchronized Map<String, TaskReport> getReportsPerSection() {
        return reportsPerSection;
    }

    /**
     * Sets number of tasks in the buffer.
     *
     * @param buffer buffer of tasks
     */
    @Override
    public synchronized void setBufferTasks(final long buffer) {
        bufferTime.setBuffer(buffer);
    }

    /**
     * Sets time for buffer tasks.
     *
     * @param time time in nanoseconds
     */
    @Override
    public synchronized void setBufferTimeNanoSec(final long time) {
        bufferTime.setTimeNanoSec(time);
    }

    /**
     * Gets number of tasks in the buffer.
     *
     * @return number of tasks in the buffer
     */
    @Override
    public synchronized long getBufferTasks() {
        return bufferTime.getBuffer();
    }

    /**
     * Gets time for buffer tasks.
     *
     * @return time in nanoseconds
     */
    @Override
    public synchronized long getBufferTimeNanoSec() {
        return bufferTime.getTimeNanoSec();
    }

}
