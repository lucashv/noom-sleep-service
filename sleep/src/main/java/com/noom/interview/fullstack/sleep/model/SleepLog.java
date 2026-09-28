package com.noom.interview.fullstack.sleep.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class SleepLog {
    private UUID id;
    private User user;
    private LocalDate sleepDate;
    @Setter(AccessLevel.NONE)
    private TimeInBedInterval timeInBedInterval;
    @Setter(AccessLevel.NONE)
    private TotalTimeInBed totalTimeInBed;
    private Feeling feeling;

    public SleepLog setTimeInBedInterval(LocalDateTime startedAt, LocalDateTime endedAt) {
        var interval = new TimeInBedInterval(startedAt, endedAt);
        var totalTime = new TotalTimeInBed(interval.duration());
        this.timeInBedInterval = interval;
        this.totalTimeInBed = totalTime;
        return this;
    }
}
