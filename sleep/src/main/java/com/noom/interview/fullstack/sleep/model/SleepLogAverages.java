package com.noom.interview.fullstack.sleep.model;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Getter
@Accessors(chain = true)
public class SleepLogAverages {
    private final LocalDate periodFrom;
    private final LocalDate periodTo;
    private final List<SleepLog> sleepLogList;
    private Duration averageTotalTimeInBed;
    private LocalTime averageTimeGoToBed;
    private LocalTime averageTimeWakeUp;
    private final Map<Feeling, Integer> feelingFrequencies;

    public SleepLogAverages(
            LocalDate periodFrom,
            LocalDate periodTo,
            List<SleepLog> sleepLogList) {
        this.periodFrom = periodFrom;
        this.periodTo = periodTo;
        this.sleepLogList = sleepLogList;
        this.feelingFrequencies = new HashMap<>();

        calculateAverages();
    }

    private void calculateAverages() {
        if (sleepLogList.isEmpty()) {
            throw new IllegalArgumentException("At least one sleep log is required");
        }

        var totalTimeInBed = Duration.ZERO;

        for (Feeling feeling : Feeling.values()) {
            feelingFrequencies.put(feeling, 0);
        }

        for (SleepLog sleepLog : sleepLogList) {
            totalTimeInBed = totalTimeInBed.plus(sleepLog.getTotalTimeInBed().duration());
            feelingFrequencies.computeIfPresent(sleepLog.getFeeling(), (feeling, count) -> count + 1);
        }

        averageTotalTimeInBed = totalTimeInBed.dividedBy(sleepLogList.size());
        averageTimeGoToBed = circularAverageTime(
                sleepLogList,
                log -> log.getTimeInBedInterval().startedAt().toLocalTime());
        averageTimeWakeUp = circularAverageTime(
                sleepLogList,
                log -> log.getTimeInBedInterval().endedAt().toLocalTime());
    }

    private LocalTime circularAverageTime(
            List<SleepLog> logs,
            Function<SleepLog, LocalTime> timeOfDay) {
        double sinSum = 0;
        double cosSum = 0;

        for (SleepLog log : logs) {
            double angle = 2 * Math.PI * timeOfDay.apply(log).toSecondOfDay() / 86_400;
            sinSum += Math.sin(angle);
            cosSum += Math.cos(angle);
        }

        double angle = Math.atan2(sinSum, cosSum);
        if (angle < 0) {
            angle += 2 * Math.PI;
        }

        long seconds = Math.round(angle * 86_400 / (2 * Math.PI)) % 86_400;
        return LocalTime.ofSecondOfDay(seconds);
    }

}
