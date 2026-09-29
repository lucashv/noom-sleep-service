package com.noom.interview.fullstack.sleep.dto;

import com.noom.interview.fullstack.sleep.model.Feeling;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class CreateSleepLogResponse {
    private UUID id;
    private Date from;
    private Date to;
    private Feeling feeling;
}
