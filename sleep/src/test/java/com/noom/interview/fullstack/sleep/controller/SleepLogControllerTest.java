package com.noom.interview.fullstack.sleep.controller;

import com.noom.interview.fullstack.sleep.model.Feeling;
import com.noom.interview.fullstack.sleep.model.SleepLog;
import com.noom.interview.fullstack.sleep.model.SleepLogAverages;
import com.noom.interview.fullstack.sleep.model.User;
import com.noom.interview.fullstack.sleep.service.SleepLogService;
import com.noom.interview.fullstack.sleep.service.SleepLogServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.dao.DataIntegrityViolationException;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SleepLogControllerTest {

    private static final UUID USER_ID = UUID.fromString("a87d8c2c-481c-4c35-9c91-ecc66043fc7d");
    private static final UUID LOG_ID = UUID.fromString("ad47b8dd-e8cb-4b72-ae1a-b4ee6f12a8be");
    private static final LocalDate TODAY = LocalDate.of(2025, 2, 15);

    @Mock
    private SleepLogService sleepLogService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        var controller = new SleepLogController(sleepLogService,
                Clock.fixed(Instant.parse("2025-02-15T12:00:00Z"), ZoneOffset.UTC));
        var validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ApiExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void createsLogFromRequestAndReturnsCreatedResponse() throws Exception {
        var createdLog = sleepLog(TODAY, LocalDateTime.of(2025, 2, 14, 22, 30),
                LocalDateTime.of(2025, 2, 15, 6, 45), Feeling.GOOD);
        createdLog.setId(LOG_ID);
        when(sleepLogService.createSleepLog(any(SleepLog.class))).thenReturn(createdLog);

        mockMvc.perform(post("/sleeplogs")
                        .header("X-User-Id", USER_ID)
                        .contentType("application/json")
                        .content("""
                                {
                                  "from": "2025-02-14T22:30:00",
                                  "to": "2025-02-15T06:45:00",
                                  "feeling": "GOOD"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(LOG_ID.toString()))
                .andExpect(jsonPath("$.sleepDate").value("2025-02-15"))
                .andExpect(jsonPath("$.timeInBedInterval.from").value("2025-02-14T22:30:00"))
                .andExpect(jsonPath("$.timeInBedInterval.to").value("2025-02-15T06:45:00"))
                .andExpect(jsonPath("$.totalTimeInBedMinutes").value(495))
                .andExpect(jsonPath("$.feeling").value("GOOD"));

        var sleepLogCaptor = ArgumentCaptor.forClass(SleepLog.class);
        verify(sleepLogService).createSleepLog(sleepLogCaptor.capture());
        assertThat(sleepLogCaptor.getValue().getUser().getId()).isEqualTo(USER_ID);
        assertThat(sleepLogCaptor.getValue().getSleepDate()).isEqualTo(TODAY);
    }

    @Test
    void returnsLastNightSleepLog() throws Exception {
        when(sleepLogService.getLastNightSleepLog(USER_ID))
                .thenReturn(sleepLog(TODAY, LocalDateTime.of(2025, 2, 14, 22, 30),
                        LocalDateTime.of(2025, 2, 15, 6, 45), Feeling.OK).setId(LOG_ID));

        mockMvc.perform(get("/sleeplogs").header("X-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(LOG_ID.toString()))
                .andExpect(jsonPath("$.sleepDate").value("2025-02-15"))
                .andExpect(jsonPath("$.timeInBedInterval.from").value("2025-02-14T22:30:00"))
                .andExpect(jsonPath("$.timeInBedInterval.to").value("2025-02-15T06:45:00"))
                .andExpect(jsonPath("$.totalTimeInBedMinutes").value(495))
                .andExpect(jsonPath("$.feeling").value("OK"));
    }

    @Test
    void returnsLastThirtyDayAverages() throws Exception {
        var frequencies = new HashMap<>(Map.of(
                Feeling.BAD, 1,
                Feeling.OK, 2,
                Feeling.GOOD, 3));
        var averages = new SleepLogAverages(TODAY.minusDays(29), TODAY,
                java.util.List.of(
                        sleepLog(TODAY, LocalDateTime.of(2025, 2, 14, 22, 30),
                                LocalDateTime.of(2025, 2, 15, 6, 30), Feeling.GOOD)));
        frequencies.forEach(averages.getFeelingFrequencies()::put);
        when(sleepLogService.getLast30DaysAverages(USER_ID)).thenReturn(averages);

        mockMvc.perform(get("/sleeplogs/averages").header("X-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.periodFrom").value("2025-01-17"))
                .andExpect(jsonPath("$.periodTo").value("2025-02-15"))
                .andExpect(jsonPath("$.averageTotalTimeInBedMinutes").value(480))
                .andExpect(jsonPath("$.averageTimeGoToBed").value("22:30:00"))
                .andExpect(jsonPath("$.averageTimeWakeUp").value("06:30:00"))
                .andExpect(jsonPath("$.feelingFrequencies.BAD").value(1))
                .andExpect(jsonPath("$.feelingFrequencies.OK").value(2))
                .andExpect(jsonPath("$.feelingFrequencies.GOOD").value(3));
    }

    @Test
    void rejectsMissingRequestFields() throws Exception {
        mockMvc.perform(post("/sleeplogs")
                        .header("X-User-Id", USER_ID)
                        .contentType("application/json")
                        .content("{\"feeling\":\"GOOD\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request"));
    }

    @Test
    void rejectsInvalidTimeInterval() throws Exception {
        mockMvc.perform(post("/sleeplogs")
                        .header("X-User-Id", USER_ID)
                        .contentType("application/json")
                        .content("""
                                {
                                  "from": "2025-02-15T06:45:00",
                                  "to": "2025-02-14T22:30:00",
                                  "feeling": "GOOD"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request"));
    }

    @Test
    void rejectsUnknownFeeling() throws Exception {
        mockMvc.perform(post("/sleeplogs")
                        .header("X-User-Id", USER_ID)
                        .contentType("application/json")
                        .content("""
                                {
                                  "from": "2025-02-14T22:30:00",
                                  "to": "2025-02-15T06:45:00",
                                  "feeling": "EXCELLENT"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request"));
    }

    @Test
    void rejectsRequestWithoutUserHeader() throws Exception {
        mockMvc.perform(get("/sleeplogs"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request"));
    }

    @Test
    void mapsMissingSleepLogToNotFound() throws Exception {
        when(sleepLogService.getLastNightSleepLog(USER_ID))
                .thenThrow(new SleepLogServiceException(
                        "No SleepLog found for last night", SleepLogServiceException.Type.NOT_FOUND));

        mockMvc.perform(get("/sleeplogs").header("X-User-Id", USER_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No SleepLog found for last night"));
    }

    @Test
    void mapsDuplicateSleepLogToConflict() throws Exception {
        when(sleepLogService.createSleepLog(any(SleepLog.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate sleep log"));

        mockMvc.perform(post("/sleeplogs")
                        .header("X-User-Id", USER_ID)
                        .contentType("application/json")
                        .content("""
                                {
                                  "from": "2025-02-14T22:30:00",
                                  "to": "2025-02-15T06:45:00",
                                  "feeling": "GOOD"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value("A sleep log already exists for this user and sleep date"));
    }

    private SleepLog sleepLog(
            LocalDate sleepDate,
            LocalDateTime from,
            LocalDateTime to,
            Feeling feeling) {
        return new SleepLog()
                .setUser(new User().setId(USER_ID))
                .setSleepDate(sleepDate)
                .setTimeInBedInterval(from, to)
                .setFeeling(feeling);
    }
}
