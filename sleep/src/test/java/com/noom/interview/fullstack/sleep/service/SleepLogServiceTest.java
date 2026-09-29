package com.noom.interview.fullstack.sleep.service;

import com.noom.interview.fullstack.sleep.model.Feeling;
import com.noom.interview.fullstack.sleep.model.SleepLog;
import com.noom.interview.fullstack.sleep.model.User;
import com.noom.interview.fullstack.sleep.repository.SleepLogRepository;
import com.noom.interview.fullstack.sleep.repository.UserRepository;
import com.noom.interview.fullstack.sleep.repository.entity.SleepLogEntity;
import com.noom.interview.fullstack.sleep.repository.entity.UserEntity;
import com.noom.interview.fullstack.sleep.service.mapper.SleepLogModelMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SleepLogServiceTest {

    private static final UUID USER_ID = UUID.fromString("a87d8c2c-481c-4c35-9c91-ecc66043fc7d");
    private static final LocalDate TODAY = LocalDate.of(2025, 2, 15);
    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2025-02-15T12:00:00Z"), ZoneOffset.UTC);

    @Mock
    private SleepLogRepository sleepLogRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SleepLogModelMapper sleepLogModelMapper;

    private SleepLogService sleepLogService;

    @BeforeEach
    void setUp() {
        sleepLogService = new SleepLogService(
                sleepLogRepository, userRepository, sleepLogModelMapper, FIXED_CLOCK);
    }

    @Test
    void createsSleepLogAndReturnsIt() throws Exception {
        var sleepLog = sleepLog(TODAY, Feeling.GOOD);
        sleepLog.setUser(new User().setId(USER_ID));
        var entity = new SleepLogEntity().setUser(new UserEntity().setId(USER_ID));
        var insertedEntity = new SleepLogEntity().setId(UUID.randomUUID());
        var returnedSleepLog = sleepLog(TODAY, Feeling.GOOD);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(new UserEntity().setId(USER_ID)));
        when(sleepLogModelMapper.modelToEntity(sleepLog)).thenReturn(entity);
        when(sleepLogRepository.insert(entity)).thenReturn(insertedEntity);
        when(sleepLogModelMapper.entityToModel(insertedEntity)).thenReturn(returnedSleepLog);

        var result = sleepLogService.createSleepLog(sleepLog);

        assertThat(result).isSameAs(returnedSleepLog);
        verify(userRepository).findById(USER_ID);
        verify(sleepLogRepository).insert(entity);
    }

    @Test
    void rejectsNullSleepLog() {
        assertThatThrownBy(() -> sleepLogService.createSleepLog(null))
                .isInstanceOf(SleepLogServiceException.class)
                .hasMessage("sleepLog should not be null");

        verifyNoInteractions(userRepository, sleepLogRepository, sleepLogModelMapper);
    }

    @Test
    void rejectsSleepLogWithoutAnExistingUser() {
        var sleepLog = sleepLog(TODAY, Feeling.GOOD);
        sleepLog.setUser(new User().setId(USER_ID));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sleepLogService.createSleepLog(sleepLog))
                .isInstanceOf(SleepLogServiceException.class)
                .hasMessage("A valid user is required");

        verifyNoInteractions(sleepLogRepository, sleepLogModelMapper);
    }

    @Test
    void rejectsSleepLogWhoseSleepDateIsNotToday() {
        var sleepLog = sleepLog(TODAY.minusDays(1), Feeling.GOOD);
        sleepLog.setUser(new User().setId(USER_ID));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(new UserEntity().setId(USER_ID)));

        assertThatThrownBy(() -> sleepLogService.createSleepLog(sleepLog))
                .isInstanceOf(SleepLogServiceException.class)
                .hasMessage("Sleep date must be today");

        verifyNoInteractions(sleepLogRepository, sleepLogModelMapper);
    }

    @Test
    void getsLastNightSleepLogForToday() throws Exception {
        var entity = new SleepLogEntity();
        var expected = sleepLog(TODAY, Feeling.OK);
        when(sleepLogRepository.filterByUserAndSleepDateRange(USER_ID, TODAY, TODAY))
                .thenReturn(List.of(entity));
        when(sleepLogModelMapper.entityToModel(entity)).thenReturn(expected);

        var result = sleepLogService.getLastNightSleepLog(USER_ID);

        assertThat(result).isSameAs(expected);
        verify(sleepLogRepository).filterByUserAndSleepDateRange(USER_ID, TODAY, TODAY);
    }

    @Test
    void throwsWhenLastNightSleepLogDoesNotExist() {
        when(sleepLogRepository.filterByUserAndSleepDateRange(USER_ID, TODAY, TODAY))
                .thenReturn(List.of());

        assertThatThrownBy(() -> sleepLogService.getLastNightSleepLog(USER_ID))
                .isInstanceOf(SleepLogServiceException.class)
                .hasMessage("No SleepLog found for last night");
    }

    @Test
    void getsAveragesForTheInclusiveLastThirtyDays() throws Exception {
        var from = TODAY.minusDays(29);
        var entity = new SleepLogEntity();
        var log = sleepLog(TODAY, Feeling.GOOD);
        when(sleepLogRepository.filterByUserAndSleepDateRange(USER_ID, from, TODAY))
                .thenReturn(List.of(entity));
        when(sleepLogModelMapper.entityToModel(entity)).thenReturn(log);

        var averages = sleepLogService.getLast30DaysAverages(USER_ID);

        assertThat(averages.getPeriodFrom()).isEqualTo(from);
        assertThat(averages.getPeriodTo()).isEqualTo(TODAY);
        assertThat(averages.getSleepLogList()).containsExactly(log);
        assertThat(averages.getFeelingFrequencies()).containsEntry(Feeling.GOOD, 1);
        verify(sleepLogRepository).filterByUserAndSleepDateRange(USER_ID, from, TODAY);
    }

    @Test
    void throwsWhenThereAreNoSleepLogsInLastThirtyDays() {
        var from = TODAY.minusDays(29);
        when(sleepLogRepository.filterByUserAndSleepDateRange(USER_ID, from, TODAY))
                .thenReturn(List.of());

        assertThatThrownBy(() -> sleepLogService.getLast30DaysAverages(USER_ID))
                .isInstanceOf(SleepLogServiceException.class)
                .hasMessage("No SleepLog found for last 30 days");
    }

    private SleepLog sleepLog(LocalDate date, Feeling feeling) {
        return new SleepLog()
                .setSleepDate(date)
                .setFeeling(feeling)
                .setTimeInBedInterval(
                        LocalDateTime.of(date, LocalTime.of(22, 0)),
                        LocalDateTime.of(date.plusDays(1), LocalTime.of(6, 0)));
    }
}
