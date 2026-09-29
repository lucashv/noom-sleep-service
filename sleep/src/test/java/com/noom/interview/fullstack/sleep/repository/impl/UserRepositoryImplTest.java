package com.noom.interview.fullstack.sleep.repository.impl;

import com.noom.interview.fullstack.sleep.SleepApplication;
import com.noom.interview.fullstack.sleep.repository.UserRepository;
import com.noom.interview.fullstack.sleep.repository.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles(SleepApplication.UNIT_TEST_PROFILE)
@Transactional
public class UserRepositoryImplTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void insertPersistsUserAndReturnsItsId() {
        var inserted = userRepository.insert(new UserEntity().setUsername("test-user"));

        assertThat(inserted.getId()).isNotNull();
        assertThat(inserted.getUsername()).isEqualTo("test-user");
        assertThat(userRepository.findById(inserted.getId()))
                .contains(new UserEntity().setId(inserted.getId()).setUsername("test-user"));
    }

    @Test
    void findByIdReturnsEmptyWhenUserDoesNotExist() {
        assertThat(userRepository.findById(UUID.randomUUID())).isEmpty();
    }

    @Test
    void findByIdReturnsOnlyRequestedUser() {
        var requested = userRepository.insert(new UserEntity().setUsername("requested-user"));
        userRepository.insert(new UserEntity().setUsername("other-user"));

        assertThat(userRepository.findById(requested.getId()))
                .contains(new UserEntity()
                        .setId(requested.getId())
                        .setUsername("requested-user"));
    }
}
