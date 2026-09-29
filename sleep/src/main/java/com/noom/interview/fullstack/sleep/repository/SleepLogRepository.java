package com.noom.interview.fullstack.sleep.repository;

import com.noom.interview.fullstack.sleep.repository.entity.SleepLogEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface SleepLogRepository {

    SleepLogEntity insert(SleepLogEntity entity);

    List<SleepLogEntity> filterByUserAndSleepDateRange(UUID userId, LocalDate from, LocalDate to);
}
