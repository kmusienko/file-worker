package com.sysgears.statistics;

/**
 * Report about completed and total tasks.
 */
public class TaskReport {

    /**
     * Number of completed tasks.
     */
    private long completed;

    /**
     * Number of total tasks.
     */
    private long total;

    /**
     * Initializes completed and total tasks fields.
     *
     * @param completed completed tasks
     * @param total total tasks
     */
    public TaskReport(long completed, long total) {
        this.completed = completed;
        this.total = total;
    }

    public long getCompleted() {
        return completed;
    }

    public long getTotal() {
        return total;
    }

    public void setCompleted(long completed) {
        this.completed = completed;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public void addCompletedTasks(long completed) {
        this.completed = this.completed + completed;
    }

    @Override
    public String toString() {
        return "TaskReport{" +
                "completed=" + completed +
                ", total=" + total +
                '}';
    }
}
