package com.sysgears.statistics;

import java.util.Map;

public class ProgressPrinter extends Thread {

    private StatisticsService statisticsService = new StatisticsServiceImpl();

    private TaskTracker taskTracker;

    public ProgressPrinter(TaskTracker taskTracker) {
        this.taskTracker = taskTracker;
    }

    @Override
    public void run() {
        int totalProgress = 0;

        while (totalProgress < 100) {
            try {
                sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            long completed = taskTracker.getCompletedTasks();
            long all = taskTracker.getTotalTasks();
            totalProgress = statisticsService.calculateProgress(completed, all);
            Map<String, Integer> progressPerSection = statisticsService
                    .calculateProgressPerSection(taskTracker.getReportsPerSection());

            StringBuilder progress = new StringBuilder();
            progress.append("Total progress: ").append(totalProgress).append("%, ");
            progressPerSection.forEach((name, percent) ->
                    progress.append(name).append(": ").append(percent).append("%, "));

            System.out.println(progress);
        }
    }
}
//split -p /home/konstantinmusienko/internship/SplMerge/myVideo.avi -s 10M
//split -p /home/konstantinmusienko/internship/SplMerge/huge.mp4 -s 1000M
//merge -p /home/konstantinmusienko/internship/SplMerge/parts