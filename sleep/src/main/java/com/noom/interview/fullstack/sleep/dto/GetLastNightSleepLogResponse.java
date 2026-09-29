package com.noom.interview.fullstack.sleep.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GetLastNightSleepLogResponse {
    private String sleepDate;
    private String totalTimeInBed;
    private String timeInBedInterval;
    private String feeling;
}
