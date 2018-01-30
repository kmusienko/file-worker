package com.sysgears.statistics;

import org.apache.log4j.Logger;

import java.util.Map;

public class ProgressPrinter extends Thread {

    private static final Logger logger = Logger.getLogger("stat-logs");

    private final String threadName = Thread.currentThread().getName();

    private StatisticsService statisticsService = new StatisticsServiceImpl();

    private TaskTracker taskTracker;

    private String command;

    public ProgressPrinter(TaskTracker taskTracker, String command) {
        this.taskTracker = taskTracker;
        this.command = command;
    }

    @Override
    public void run() {
        logger.trace("ProgressPrinter started." + this);
        int totalProgress = 0;
        while (totalProgress < 100) {
            try {
                sleep(800);
            } catch (InterruptedException e) {
                throw new RuntimeException(e.getMessage());
            }
            long completed = taskTracker.getCompletedTasks();
            long all = taskTracker.getTotalTasks();
            totalProgress = statisticsService.calculateProgress(completed, all);
            long timeRemaining = statisticsService.calculateTimeRemaining(taskTracker.getBufferTasks(), taskTracker
                    .getBufferTime(), all - completed);
            Map<String, Integer> progressPerSection = statisticsService
                    .calculateProgressPerSection(taskTracker.getReportsPerSection());
            logger.trace("Completed tasks: " + completed + "." + "Total tasks: " + all + "."
                                 + "Total progress: " + totalProgress + "." + "Time remaining: " + timeRemaining + "."
                                 + "Progress per section: " + progressPerSection + this);
            StringBuilder progress = new StringBuilder();
            progress.append("Total progress: ").append(totalProgress).append("%, ");
            progressPerSection.forEach((name, percent) ->
                                               progress.append(name).append(": ").append(percent).append("%, "));
            progress.append("Time remaining: ").append(timeRemaining).append("ms");
            System.out.println(progress);
            logger.trace("Printed progress." + this);
        }
        logger.trace("ProgressPrinter completed." + this);
    }

    @Override
    public String toString() {
        return "ProgressPrinter{" +
                "threadName='" + threadName + '\'' +
                ", command='" + command + '\'' +
                '}';
    }
}
//split -p /home/konstantinmusienko/internship/SplMerge/myVideo.avi -s 10M
//split -p /home/konstantinmusienko/internship/SplMerge/huge.mp4 -s 1000M
//merge -p /home/konstantinmusienko/internship/SplMerge/parts