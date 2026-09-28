package com.noom.interview.fullstack.sleep.model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SleepModelTest {

    @Test
    void timeInBedIntervalRequiresEndAfterStart() {
        var time = LocalDateTime.of(2026, 9, 26, 22, 0);

        assertThatThrownBy(() -> new TimeInBedInterval(time, time))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void totalTimeInBedRejectsNegativeDuration() {
        assertThatThrownBy(() -> new TotalTimeInBed(Duration.ofMinutes(-1)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void sleepLogKeepsExistingValuesWhenNewIntervalIsInvalid() {
        var startedAt = LocalDateTime.of(2026, 9, 25, 22, 0);
        var endedAt = LocalDateTime.of(2026, 9, 26, 6, 0);
        var sleepLog = new SleepLog().setTimeInBedInterval(startedAt, endedAt);
        var originalInterval = sleepLog.getTimeInBedInterval();
        var originalTotalTime = sleepLog.getTotalTimeInBed();

        assertThatThrownBy(() -> sleepLog.setTimeInBedInterval(endedAt, startedAt))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(sleepLog.getTimeInBedInterval()).isSameAs(originalInterval);
        assertThat(sleepLog.getTotalTimeInBed()).isSameAs(originalTotalTime);
    }

    @Test
    void sleepLogCalculatesTotalTimeFromInterval() {
        var startedAt = LocalDateTime.of(2026, 9, 25, 22, 0);
        var endedAt = LocalDateTime.of(2026, 9, 26, 6, 0);

        var sleepLog = new SleepLog().setTimeInBedInterval(startedAt, endedAt);

        assertThat(sleepLog.getTotalTimeInBed().duration()).isEqualTo(Duration.ofHours(8));
    }
}
