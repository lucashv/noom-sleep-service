package com.noom.interview.fullstack.sleep.repository.impl;

import com.noom.interview.fullstack.sleep.SleepApplication;
import com.noom.interview.fullstack.sleep.repository.UserRepository;
import com.noom.interview.fullstack.sleep.repository.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import org.junit.jupiter.api.Test;

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
        assertThat(userRepository.fetchAll())
                .containsExactly(new UserEntity()
                        .setId(inserted.getId())
                        .setUsername("test-user"));
    }

    @Test
    void fetchAllReturnsEmptyCollectionWhenNoUsersExist() {
        assertThat(userRepository.fetchAll()).isEmpty();
    }

    @Test
    void fetchAllReturnsEveryUser() {
        var first = userRepository.insert(new UserEntity().setUsername("first-user"));
        var second = userRepository.insert(new UserEntity().setUsername("second-user"));

        assertThat(userRepository.fetchAll()).containsExactlyInAnyOrder(
                new UserEntity().setId(first.getId()).setUsername("first-user"),
                new UserEntity().setId(second.getId()).setUsername("second-user")
        );
    }
}
