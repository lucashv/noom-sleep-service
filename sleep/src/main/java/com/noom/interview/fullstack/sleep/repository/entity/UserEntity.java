package com.noom.interview.fullstack.sleep.repository.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserEntity {
    private Long id;
    private String username;
}
