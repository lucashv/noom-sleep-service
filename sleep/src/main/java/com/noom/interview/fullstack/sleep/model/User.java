package com.noom.interview.fullstack.sleep.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.UUID;

@Data
@Accessors(chain = true)
public class User {
    private UUID id;
    private String username;
}
