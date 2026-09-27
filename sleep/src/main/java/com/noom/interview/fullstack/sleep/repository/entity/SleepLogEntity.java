package com.noom.interview.fullstack.sleep.repository.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class SleepLogEntity {
    private UUID id;
    private UserEntity user;
    private LocalDate sleepDate;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private String feeling;
}
