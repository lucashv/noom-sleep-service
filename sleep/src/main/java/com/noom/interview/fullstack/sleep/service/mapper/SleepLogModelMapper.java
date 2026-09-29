package com.noom.interview.fullstack.sleep.service.mapper;

import com.noom.interview.fullstack.sleep.model.SleepLog;
import com.noom.interview.fullstack.sleep.repository.entity.SleepLogEntity;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {UserModelMapper.class})
public interface SleepLogModelMapper {

    @Mapping(target = "startedAt", expression = "java(sleepLog.getTimeInBedInterval().startedAt())")
    @Mapping(target = "endedAt", expression = "java(sleepLog.getTimeInBedInterval().endedAt())")
    SleepLogEntity modelToEntity(SleepLog sleepLog);

    @Mapping(target = "timeInBedInterval", ignore = true)
    @Mapping(target = "totalTimeInBed", ignore = true)
    SleepLog entityToModel(SleepLogEntity sleepLogEntity);

    @AfterMapping
    default void setTimeInBedInterval(SleepLogEntity entity, @MappingTarget SleepLog model) {
        model.setTimeInBedInterval(entity.getStartedAt(), entity.getEndedAt());
    }
}
