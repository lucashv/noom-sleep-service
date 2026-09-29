package com.noom.interview.fullstack.sleep.repository;

import com.noom.interview.fullstack.sleep.repository.entity.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    UserEntity insert(UserEntity entity);

    Optional<UserEntity> findById(UUID userId);
}
