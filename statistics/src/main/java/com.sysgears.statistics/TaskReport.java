package com.sysgears.statistics;

public class TaskReport {

    private long completed;
    private long total;

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
