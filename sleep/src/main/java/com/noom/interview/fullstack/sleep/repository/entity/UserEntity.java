package com.noom.interview.fullstack.sleep.repository.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.UUID;

@Data
@Accessors(chain = true)
public class UserEntity {
    private UUID id;
    private String username;
}
