package com.noom.interview.fullstack.sleep.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateSleepLogRequest {
    @NotNull
    private LocalDateTime from;
    @NotNull
    private LocalDateTime to;
    @NotBlank
    private String feeling;
}
