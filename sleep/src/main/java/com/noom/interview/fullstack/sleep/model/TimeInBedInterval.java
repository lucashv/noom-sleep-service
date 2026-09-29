package com.noom.interview.fullstack.sleep.model;

import java.time.Duration;
import java.time.LocalDateTime;

public record TimeInBedInterval(LocalDateTime startedAt, LocalDateTime endedAt) {
    public TimeInBedInterval {
        if (startedAt == null || endedAt == null) {
            throw new IllegalArgumentException("startedAt and endedAt should not be null");
        }
        if (!endedAt.isAfter(startedAt)) {
            throw new IllegalArgumentException("endedAt should be later than startedAt");
        }
    }

    public Duration duration() {
        return Duration.between(startedAt, endedAt);
    }
}
