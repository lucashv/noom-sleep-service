package com.noom.interview.fullstack.sleep.controller;

import com.noom.interview.fullstack.sleep.dto.CreateSleepLogRequest;
import com.noom.interview.fullstack.sleep.dto.CreateSleepLogResponse;
import com.noom.interview.fullstack.sleep.dto.GetLastNightSleepLogResponse;
import com.noom.interview.fullstack.sleep.model.SleepLog;
import com.noom.interview.fullstack.sleep.model.User;
import com.noom.interview.fullstack.sleep.service.SleepLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping("/sleeplogs")
@RequiredArgsConstructor
public class SleepLogController {

    private final UUID USER_ID = UUID.fromString("2dbcb0b3-17a2-4220-bfb1-9b7efa7e0779");

    private final SleepLogService sleepLogService;
    private final Clock clock;

    @PostMapping
    public ResponseEntity<CreateSleepLogResponse> createSleepLog(
            @RequestBody CreateSleepLogRequest sleepLogRequest) {
        try {
            var zoneId = ZoneId.systemDefault();
            var startedAt = sleepLogRequest.getFrom().toInstant().atZone(zoneId).toLocalDateTime();
            var endedAt = sleepLogRequest.getTo().toInstant().atZone(zoneId).toLocalDateTime();
            var sleepLog = new SleepLog()
                    .setUser(new User().setId(USER_ID))
                    .setSleepDate(LocalDate.now(clock))
                    .setTimeInBedInterval(startedAt, endedAt)
                    .setFeeling(sleepLogRequest.getFeeling());
            var createdSleepLog = sleepLogService.createSleepLog(sleepLog);
            return ResponseEntity.created(URI.create(""))
                    .body(new CreateSleepLogResponse()
                            .setId(createdSleepLog.getId())
                            .setFrom(
                                    Date.from(createdSleepLog.getTimeInBedInterval().startedAt()
                                            .toInstant(ZoneOffset.UTC)))
                            .setTo(Date.from(createdSleepLog.getTimeInBedInterval().endedAt()
                                    .toInstant(ZoneOffset.UTC))));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity<GetLastNightSleepLogResponse> getLastNightSleepLog() {
        try {
            var sleepLog = sleepLogService.getLastNightSleepLog(USER_ID);
            var formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.US);
            var timeInBedInterval = "%s - %s"
                    .formatted(
                            sleepLog.getTimeInBedInterval().startedAt().format(formatter),
                            sleepLog.getTimeInBedInterval().endedAt().format(formatter));
            var totalTimeInBed = "%d h %d min"
                    .formatted(
                            sleepLog.getTotalTimeInBed().duration().toHours(),
                            sleepLog.getTotalTimeInBed().duration().toMinutesPart());
            return ResponseEntity.ok(new GetLastNightSleepLogResponse()
                    .setSleepDate(sleepLog.getSleepDate().toString())
                    .setTotalTimeInBed(totalTimeInBed)
                    .setTimeInBedInterval(timeInBedInterval)
                    .setFeeling(sleepLog.getFeeling().name()));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/averages")
    public ResponseEntity<String> getLast30DaysAverages() {
        return ResponseEntity.ok("Ok");
    }
}
