package com.noom.interview.fullstack.sleep.service;

import com.noom.interview.fullstack.sleep.model.SleepLog;
import com.noom.interview.fullstack.sleep.model.SleepLogAverages;
import com.noom.interview.fullstack.sleep.repository.SleepLogRepository;
import com.noom.interview.fullstack.sleep.repository.UserRepository;
import com.noom.interview.fullstack.sleep.service.mapper.SleepLogModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SleepLogService {

    private static final String SLEEP_LOG_NOT_NULL_ERROR_MESSAGE = "sleepLog should not be null";
    private static final String VALID_USER_ERROR_MESSAGE = "A valid user is required";
    private static final String SLEEP_DATE_MUST_BE_TODAY_ERROR_MESSAGE = "Sleep date must be today";
    private static final String NO_SLEEPLOG_FOR_LAST_NIGHT_ERROR_MESSAGE = "No SleepLog found for last night";
    private static final String NO_SLEEPLOG_FOR_LAST_30_DAYS_ERROR_MESSAGE = "No SleepLog found for last 30 days";

    private final SleepLogRepository sleepLogRepository;
    private final UserRepository userRepository;
    private final SleepLogModelMapper sleepLogModelMapper;
    private final Clock clock;

    @Transactional
    public SleepLog createSleepLog(SleepLog sleepLog) throws SleepLogServiceException {
        if (sleepLog == null) {
            throw new SleepLogServiceException(SLEEP_LOG_NOT_NULL_ERROR_MESSAGE);
        }

        if (sleepLog.getUser() == null || sleepLog.getUser().getId() == null ||
                userRepository.findById(sleepLog.getUser().getId()).isEmpty()) {
            throw new SleepLogServiceException(VALID_USER_ERROR_MESSAGE);
        }

        if (!LocalDate.now(clock).equals(sleepLog.getSleepDate())) {
            throw new SleepLogServiceException(SLEEP_DATE_MUST_BE_TODAY_ERROR_MESSAGE);
        }

        var entity = sleepLogModelMapper.modelToEntity(sleepLog);
        var insertedEntity = sleepLogRepository.insert(entity);
        return sleepLogModelMapper.entityToModel(insertedEntity);
    }

    public SleepLog getLastNightSleepLog(UUID userId) throws SleepLogServiceException {
        if (userId == null) {
            throw new SleepLogServiceException(VALID_USER_ERROR_MESSAGE);
        }

        var today = LocalDate.now(clock);

        var sleepLogList = sleepLogRepository.filterByUserAndSleepDateRange(userId, today, today);

        if (sleepLogList.isEmpty()) {
            throw new SleepLogServiceException(NO_SLEEPLOG_FOR_LAST_NIGHT_ERROR_MESSAGE);
        }

        return sleepLogModelMapper.entityToModel(sleepLogList.get(0));
    }

    public SleepLogAverages getLast30DaysAverages(UUID userId) throws SleepLogServiceException {
        if (userId == null) {
            throw new SleepLogServiceException(VALID_USER_ERROR_MESSAGE);
        }

        var to = LocalDate.now(clock);
        var from = to.minusDays(29);

        var sleepLogEntityList = sleepLogRepository.filterByUserAndSleepDateRange(userId, from, to);

        if (sleepLogEntityList.isEmpty()) {
            throw new SleepLogServiceException(NO_SLEEPLOG_FOR_LAST_30_DAYS_ERROR_MESSAGE);
        }

        var sleepLogList = sleepLogEntityList
                .stream()
                .map(sleepLogModelMapper::entityToModel)
                .toList();

        return new SleepLogAverages(from, to, sleepLogList);
    }
}
