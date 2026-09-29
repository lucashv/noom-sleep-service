package com.noom.interview.fullstack.sleep.dto;

import com.noom.interview.fullstack.sleep.model.Feeling;
import lombok.Data;

import java.util.Date;

@Data
public class CreateSleepLogRequest {
    private Date from;
    private Date to;
    private Feeling feeling;
}
