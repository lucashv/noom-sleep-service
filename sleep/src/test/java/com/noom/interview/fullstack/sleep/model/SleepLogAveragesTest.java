package com.noom.interview.fullstack.sleep.model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class SleepLogAveragesTest {

    private static final LocalDate PERIOD_FROM = LocalDate.of(2025, 1, 1);
    private static final LocalDate PERIOD_TO = LocalDate.of(2025, 1, 31);

    @Test
    void calculatesAverageDurationAndTimeOfDayAndCountsFeelings() {
        var sleepLogs = List.of(
                sleepLog(PERIOD_FROM, LocalTime.of(22, 0), LocalTime.of(6, 0), Feeling.BAD),
                sleepLog(PERIOD_FROM.plusDays(1), LocalTime.MIDNIGHT, LocalTime.of(8, 0), Feeling.OK));

        var averages = new SleepLogAverages(PERIOD_FROM, PERIOD_TO, sleepLogs);

        assertThat(averages.getPeriodFrom()).isEqualTo(PERIOD_FROM);
        assertThat(averages.getPeriodTo()).isEqualTo(PERIOD_TO);
        assertThat(averages.getSleepLogList()).containsExactlyElementsOf(sleepLogs);
        assertThat(averages.getAverageTotalTimeInBed()).isEqualTo(Duration.ofHours(8));
        assertThat(averages.getAverageTimeGoToBed()).isEqualTo(LocalTime.of(23, 0));
        assertThat(averages.getAverageTimeWakeUp()).isEqualTo(LocalTime.of(7, 0));
        assertThat(averages.getFeelingFrequencies())
                .containsEntry(Feeling.BAD, 1)
                .containsEntry(Feeling.OK, 1)
                .containsEntry(Feeling.GOOD, 0);
    }

    @Test
    void calculatesBedtimeAverageAcrossMidnight() {
        var sleepLogs = List.of(
                sleepLog(PERIOD_FROM, LocalTime.of(23, 50), LocalTime.of(7, 50), Feeling.GOOD),
                sleepLog(PERIOD_FROM.plusDays(1), LocalTime.of(0, 10), LocalTime.of(8, 10), Feeling.GOOD));

        var averages = new SleepLogAverages(PERIOD_FROM, PERIOD_TO, sleepLogs);

        assertThat(averages.getAverageTimeGoToBed()).isEqualTo(LocalTime.MIDNIGHT);
        assertThat(averages.getAverageTimeWakeUp()).isEqualTo(LocalTime.of(8, 0));
    }

    @Test
    void rejectsAnEmptySleepLogList() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new SleepLogAverages(PERIOD_FROM, PERIOD_TO, List.of()))
                .withMessage("At least one sleep log is required");
    }

    private SleepLog sleepLog(LocalDate date, LocalTime bedtime, LocalTime wakeUp, Feeling feeling) {
        var wakeUpDate = wakeUp.isAfter(bedtime) ? date : date.plusDays(1);
        return new SleepLog()
                .setSleepDate(date)
                .setFeeling(feeling)
                .setTimeInBedInterval(
                        LocalDateTime.of(date, bedtime),
                        LocalDateTime.of(wakeUpDate, wakeUp));
    }
}
