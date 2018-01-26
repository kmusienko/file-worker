package com.sysgears.filesplitter.splitting;

import com.sysgears.statistics.TaskReport;
import com.sysgears.statistics.TaskTracker;

import java.util.HashMap;
import java.util.Map;

public class TaskTrackerImpl implements TaskTracker {

    private TaskReport taskReport = new TaskReport(0, 0);
    private Map<String, TaskReport> reportsPerSection = new HashMap<>();

    @Override
    public synchronized void addCompletedTasks(final long completedTasks) {
        taskReport.addCompletedTasks(completedTasks);
    }

    @Override
    public synchronized void addReportPerSection(final String name, final TaskReport taskReport) {
        this.reportsPerSection.put(name, taskReport);
    }

    @Override
    public synchronized void setTotalTasks(final long totalTasks) {
        taskReport.setTotal(totalTasks);
    }

    @Override
    public synchronized void setCompletedTasks(final long completedTasks) {
        taskReport.setCompleted(completedTasks);
    }

    @Override
    public synchronized long getCompletedTasks() {
        return taskReport.getCompleted();
    }

    @Override
    public synchronized long getTotalTasks() {
        return taskReport.getTotal();
    }

    @Override
    public synchronized Map<String, TaskReport> getReportsPerSection() {
        return reportsPerSection;
    }

}
