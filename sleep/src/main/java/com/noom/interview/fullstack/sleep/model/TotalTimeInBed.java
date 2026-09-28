package com.noom.interview.fullstack.sleep.model;

import java.time.Duration;

public record TotalTimeInBed(Duration duration) {

    public TotalTimeInBed {
        if (duration == null) {
            throw new IllegalArgumentException("Duration should not be null");
        }
        if (duration.isNegative()) {
            throw new IllegalArgumentException("Duration should not be negative");
        }
    }
}
