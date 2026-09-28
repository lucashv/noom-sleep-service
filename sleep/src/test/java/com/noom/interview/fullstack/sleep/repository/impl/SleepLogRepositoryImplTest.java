package com.noom.interview.fullstack.sleep.repository.impl;

import com.noom.interview.fullstack.sleep.SleepApplication;
import com.noom.interview.fullstack.sleep.repository.SleepLogRepository;
import com.noom.interview.fullstack.sleep.repository.UserRepository;
import com.noom.interview.fullstack.sleep.repository.entity.SleepLogEntity;
import com.noom.interview.fullstack.sleep.repository.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles(SleepApplication.UNIT_TEST_PROFILE)
@Transactional
public class SleepLogRepositoryImplTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SleepLogRepository sleepLogRepository;

    @Test
    void insertPersistsSleepLogAndReturnsItsId() {
        var user = userRepository.insert(new UserEntity().setUsername("test-user"));
        var startedAt = LocalDateTime.of(2026, 9, 25, 22, 30);
        var endedAt = LocalDateTime.of(2026, 9, 26, 6, 45);
        var sleepLog = new SleepLogEntity()
                .setUser(user)
                .setSleepDate(LocalDate.of(2026, 9, 26))
                .setStartedAt(startedAt)
                .setEndedAt(endedAt)
                .setFeeling("GOOD");

        var inserted = sleepLogRepository.insert(sleepLog);

        assertThat(inserted.getId()).isNotNull();

        var fetchedLogs = sleepLogRepository.filterByUserAndDateRange(
                user.getId(), LocalDate.of(2026, 9, 26), LocalDate.of(2026, 9, 26));
        assertThat(fetchedLogs).hasSize(1);

        var fetched = fetchedLogs.iterator().next();
        assertThat(fetched.getId()).isEqualTo(inserted.getId());
        assertThat(fetched.getUser().getId()).isEqualTo(user.getId());
        assertThat(fetched.getSleepDate()).isEqualTo(LocalDate.of(2026, 9, 26));
        assertThat(fetched.getStartedAt()).isEqualTo(startedAt);
        assertThat(fetched.getEndedAt()).isEqualTo(endedAt);
        assertThat(fetched.getFeeling()).isEqualTo("GOOD");
    }

    @Test
    void filterByUserAndDateRangeReturnsMatchingLogsAndMapsNullableFields() {
        var user = userRepository.insert(new UserEntity().setUsername("test-user"));
        var otherUser = userRepository.insert(new UserEntity().setUsername("other-user"));
        var first = sleepLogRepository.insert(new SleepLogEntity()
                .setUser(user)
                .setSleepDate(LocalDate.of(2026, 9, 25))
                .setStartedAt(LocalDateTime.of(2026, 9, 24, 22, 0))
                .setEndedAt(LocalDateTime.of(2026, 9, 25, 6, 0))
                .setFeeling("GOOD"));
        var second = sleepLogRepository.insert(new SleepLogEntity()
                .setUser(user)
                .setSleepDate(LocalDate.of(2026, 9, 26))
                .setStartedAt(LocalDateTime.of(2026, 9, 25, 23, 15))
                .setEndedAt(null)
                .setFeeling(null));
        sleepLogRepository.insert(new SleepLogEntity()
                .setUser(otherUser)
                .setSleepDate(LocalDate.of(2026, 9, 26))
                .setStartedAt(LocalDateTime.of(2026, 9, 25, 22, 0))
                .setEndedAt(LocalDateTime.of(2026, 9, 26, 6, 0))
                .setFeeling("OK"));

        var fetchedLogs = sleepLogRepository.filterByUserAndDateRange(
                user.getId(), LocalDate.of(2026, 9, 25), LocalDate.of(2026, 9, 26));

        assertThat(fetchedLogs).containsExactlyInAnyOrder(
                new SleepLogEntity()
                        .setId(first.getId())
                        .setUser(new UserEntity().setId(user.getId()))
                        .setSleepDate(LocalDate.of(2026, 9, 25))
                        .setStartedAt(LocalDateTime.of(2026, 9, 24, 22, 0))
                        .setEndedAt(LocalDateTime.of(2026, 9, 25, 6, 0))
                        .setFeeling("GOOD"),
                new SleepLogEntity()
                        .setId(second.getId())
                        .setUser(new UserEntity().setId(user.getId()))
                        .setSleepDate(LocalDate.of(2026, 9, 26))
                        .setStartedAt(LocalDateTime.of(2026, 9, 25, 23, 15))
                        .setEndedAt(null)
                        .setFeeling(null)
        );
    }

    @Test
    void filterByUserAndDateRangeReturnsEmptyWhenNoSleepLogsMatch() {
        assertThat(sleepLogRepository.filterByUserAndDateRange(
                UUID.randomUUID(), LocalDate.of(2026, 9, 25), LocalDate.of(2026, 9, 26)))
                .isEmpty();
    }

    @Test
    void insertRejectsSecondSleepLogForSameUserAndDate() {
        var user = userRepository.insert(new UserEntity().setUsername("test-user"));
        var sleepDate = LocalDate.of(2026, 9, 26);
        var sleepLog = new SleepLogEntity()
                .setUser(user)
                .setSleepDate(sleepDate)
                .setStartedAt(LocalDateTime.of(2026, 9, 25, 22, 30))
                .setEndedAt(LocalDateTime.of(2026, 9, 26, 6, 45))
                .setFeeling("GOOD");
        sleepLogRepository.insert(sleepLog);

        assertThatThrownBy(() -> sleepLogRepository.insert(new SleepLogEntity()
                .setUser(user)
                .setSleepDate(sleepDate)
                .setStartedAt(LocalDateTime.of(2026, 9, 25, 23, 0))
                .setEndedAt(LocalDateTime.of(2026, 9, 26, 7, 0))
                .setFeeling("OK")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void insertRejectsSleepLogForUnknownUser() {
        var sleepLog = new SleepLogEntity()
                .setUser(new UserEntity().setId(UUID.randomUUID()))
                .setSleepDate(LocalDate.of(2026, 9, 26))
                .setStartedAt(LocalDateTime.of(2026, 9, 25, 22, 30))
                .setEndedAt(LocalDateTime.of(2026, 9, 26, 6, 45))
                .setFeeling("GOOD");

        assertThatThrownBy(() -> sleepLogRepository.insert(sleepLog))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

}
