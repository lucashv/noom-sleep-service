package com.noom.interview.fullstack.sleep.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class TimeInBedIntervalResponse {
    private LocalDateTime from;
    private LocalDateTime to;
}
