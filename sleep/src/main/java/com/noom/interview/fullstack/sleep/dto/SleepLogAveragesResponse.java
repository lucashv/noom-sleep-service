package com.noom.interview.fullstack.sleep.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

@Data
@Accessors(chain = true)
public class SleepLogAveragesResponse {
    private LocalDate periodFrom;
    private LocalDate periodTo;
    private long averageTotalTimeInBedMinutes;
    private LocalTime averageTimeGoToBed;
    private LocalTime averageTimeWakeUp;
    private Map<String, Integer> feelingFrequencies;
}
