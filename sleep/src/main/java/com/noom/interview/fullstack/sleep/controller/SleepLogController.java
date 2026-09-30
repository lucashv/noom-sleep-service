package com.noom.interview.fullstack.sleep.controller;

import com.noom.interview.fullstack.sleep.dto.CreateSleepLogRequest;
import com.noom.interview.fullstack.sleep.dto.CreateSleepLogResponse;
import com.noom.interview.fullstack.sleep.dto.GetLastNightSleepLogResponse;
import com.noom.interview.fullstack.sleep.dto.SleepLogAveragesResponse;
import com.noom.interview.fullstack.sleep.dto.TimeInBedIntervalResponse;
import com.noom.interview.fullstack.sleep.model.Feeling;
import com.noom.interview.fullstack.sleep.model.SleepLog;
import com.noom.interview.fullstack.sleep.model.User;
import com.noom.interview.fullstack.sleep.service.SleepLogService;
import com.noom.interview.fullstack.sleep.service.SleepLogServiceException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/sleeplogs")
@RequiredArgsConstructor
public class SleepLogController {

    private final SleepLogService sleepLogService;
    private final Clock clock;

    @PostMapping
    public ResponseEntity<CreateSleepLogResponse> createSleepLog(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody CreateSleepLogRequest request) throws SleepLogServiceException {
        var sleepLog = new SleepLog()
                .setUser(new User().setId(userId))
                .setSleepDate(LocalDate.now(clock))
                .setTimeInBedInterval(request.getFrom(), request.getTo())
                .setFeeling(Feeling.valueOf(request.getFeeling()));

        var created = sleepLogService.createSleepLog(sleepLog);
        var response = new CreateSleepLogResponse()
                .setId(created.getId())
                .setSleepDate(created.getSleepDate())
                .setTimeInBedInterval(new TimeInBedIntervalResponse()
                        .setFrom(created.getTimeInBedInterval().startedAt())
                        .setTo(created.getTimeInBedInterval().endedAt()))
                .setTotalTimeInBedMinutes(created.getTotalTimeInBed().duration().toMinutes())
                .setFeeling(created.getFeeling().name());

        return ResponseEntity.created(URI.create("/sleeplogs")).body(response);
    }

    @GetMapping
    public ResponseEntity<GetLastNightSleepLogResponse> getLastNightSleepLog(
            @RequestHeader("X-User-Id") UUID userId) throws SleepLogServiceException {
        var sleepLog = sleepLogService.getLastNightSleepLog(userId);
        return ResponseEntity.ok(toLastNightResponse(sleepLog));
    }

    @GetMapping("/averages")
    public ResponseEntity<SleepLogAveragesResponse> getLast30DaysAverages(
            @RequestHeader("X-User-Id") UUID userId) throws SleepLogServiceException {
        var averages = sleepLogService.getLast30DaysAverages(userId);
        var response = new SleepLogAveragesResponse()
                .setPeriodFrom(averages.getPeriodFrom())
                .setPeriodTo(averages.getPeriodTo())
                .setAverageTotalTimeInBedMinutes(averages.getAverageTotalTimeInBed().toMinutes())
                .setAverageTimeGoToBed(averages.getAverageTimeGoToBed())
                .setAverageTimeWakeUp(averages.getAverageTimeWakeUp())
                .setFeelingFrequencies(averages.getFeelingFrequencies().entrySet().stream()
                        .collect(java.util.stream.Collectors.toMap(
                                entry -> entry.getKey().name(),
                                java.util.Map.Entry::getValue)));
        return ResponseEntity.ok(response);
    }

    private GetLastNightSleepLogResponse toLastNightResponse(SleepLog sleepLog) {
        LocalDateTime startedAt = sleepLog.getTimeInBedInterval().startedAt();
        LocalDateTime endedAt = sleepLog.getTimeInBedInterval().endedAt();
        return new GetLastNightSleepLogResponse()
                .setId(sleepLog.getId())
                .setSleepDate(sleepLog.getSleepDate())
                .setTimeInBedInterval(new TimeInBedIntervalResponse()
                        .setFrom(startedAt)
                        .setTo(endedAt))
                .setTotalTimeInBedMinutes(sleepLog.getTotalTimeInBed().duration().toMinutes())
                .setFeeling(sleepLog.getFeeling().name());
    }
}
