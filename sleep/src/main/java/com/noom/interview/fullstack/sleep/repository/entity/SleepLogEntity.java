package com.noom.interview.fullstack.sleep.repository.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class SleepLogEntity {
    private Long id;
    private UserEntity user;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private Integer feeling;
}
