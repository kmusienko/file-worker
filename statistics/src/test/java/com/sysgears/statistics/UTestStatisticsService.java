package com.sysgears.statistics;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class UTestStatisticsService {

    private StatisticsService statisticsService;

    @BeforeClass
    public void setUp() {
        statisticsService = new StatisticsServiceImpl();
    }

    @Test
    public void testCalculateProgress() {
        //Arrange
        final long completedTasks = 25;
        final long totalTasks = 125;
        final long expectedProgress = 20;

        //Act
        final long actualProgress = statisticsService.calculateProgress(completedTasks, totalTasks);

        //Assert
        Assert.assertEquals(actualProgress, expectedProgress);
    }

    @Test
    public void testCalculateTimeRemaining() {
        //Arrange
        final long bufferTasks = 5;
        final long bufferTimeNanoSec = 40_000_000;
        final long remainingTasks = 15;
        final long expectedTimeRemainingMillisec = (remainingTasks / bufferTasks * bufferTimeNanoSec) / 1_000_000;

        //Act
        final long actualTimeRemainingMillisec = statisticsService.calculateTimeRemaining(bufferTasks, bufferTimeNanoSec,
                                                                                          remainingTasks);

        //Assert
        Assert.assertEquals(actualTimeRemainingMillisec, expectedTimeRemainingMillisec);
    }
}
