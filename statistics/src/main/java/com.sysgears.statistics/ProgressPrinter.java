package com.sysgears.statistics;

import org.apache.log4j.Logger;

import java.util.Map;

public class ProgressPrinter extends Thread {

    private StatisticsService statisticsService = new StatisticsServiceImpl();

    private TaskTracker taskTracker;

    private String command;

    public ProgressPrinter(TaskTracker taskTracker, String command) {
        this.taskTracker = taskTracker;
        this.command = command;
    }

    @Override
    public void run() {
        Logger logger = Logger.getLogger("stat-logs");
        String userCommand = "User command: " + command;
        logger.trace("Executing ProgressPrinter's run method." + userCommand);
        int totalProgress = 0;

        while (totalProgress < 100) {
            logger.trace("Sleeping 400ms." + userCommand);
            try {
                sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.trace("Getting completed tasks." + userCommand);
            long completed = taskTracker.getCompletedTasks();
            logger.trace("Completed tasks: " + completed + "." + userCommand);
            logger.trace("Getting total tasks." + userCommand);
            long all = taskTracker.getTotalTasks();
            logger.trace("Total tasks: " + all + "." + userCommand);
            logger.trace("Calculating total progress." + userCommand);
            totalProgress = statisticsService.calculateProgress(completed, all);
            logger.trace("Total progress: " + totalProgress + "." + userCommand);
            logger.trace("Calculating time remaining." + userCommand);
            long timeRemaining = statisticsService.calculateTimeRemaining(taskTracker.getBufferTasks(), taskTracker
                    .getBufferTime(), all - completed);
            logger.trace("Time remaining: " + timeRemaining + "." + userCommand);
            logger.trace("Calculating progress per section." + userCommand);
            Map<String, Integer> progressPerSection = statisticsService
                    .calculateProgressPerSection(taskTracker.getReportsPerSection());
            logger.trace("Progress per section: " + progressPerSection + "." + userCommand);
            StringBuilder progress = new StringBuilder();
            logger.trace("Building progress string." + userCommand);
            progress.append("Total progress: ").append(totalProgress).append("%, ");
            progressPerSection.forEach((name, percent) ->
                                               progress.append(name).append(": ").append(percent).append("%, "));
            progress.append("Time remaining: ").append(timeRemaining).append("ms");
            logger.trace("Printing progress." + userCommand);
            System.out.println(progress);
        }
    }
}
//split -p /home/konstantinmusienko/internship/SplMerge/myVideo.avi -s 10M
//split -p /home/konstantinmusienko/internship/SplMerge/huge.mp4 -s 1000M
//merge -p /home/konstantinmusienko/internship/SplMerge/parts