package com.noom.interview.fullstack.sleep.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class GetLastNightSleepLogResponse {
    private UUID id;
    private LocalDate sleepDate;
    private TimeInBedIntervalResponse timeInBedInterval;
    private long totalTimeInBedMinutes;
    private String feeling;
}
