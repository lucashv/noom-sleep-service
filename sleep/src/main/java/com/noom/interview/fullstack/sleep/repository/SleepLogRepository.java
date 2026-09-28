package com.noom.interview.fullstack.sleep.repository;

import com.noom.interview.fullstack.sleep.repository.entity.SleepLogEntity;

import java.time.LocalDate;
import java.util.Collection;
import java.util.UUID;

public interface SleepLogRepository {

    SleepLogEntity insert(SleepLogEntity entity);

    Collection<SleepLogEntity> filterByUserAndDateRange(UUID userId, LocalDate from, LocalDate to);
}
