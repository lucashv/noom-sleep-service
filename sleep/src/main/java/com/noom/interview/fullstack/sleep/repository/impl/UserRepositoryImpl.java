package com.noom.interview.fullstack.sleep.repository.impl;

import com.noom.interview.fullstack.sleep.repository.UserRepository;
import com.noom.interview.fullstack.sleep.repository.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private static final String ID_COLUMN = "id";
    private static final String USERNAME_COLUMN = "username";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public UserEntity insert(UserEntity entity) {
        var sql = "insert into t_user (%s, %s) values (?, ?)"
                .formatted(ID_COLUMN, USERNAME_COLUMN);
        var newId = UUID.randomUUID();
        jdbcTemplate.update(sql, newId, entity.getUsername());
        return entity.setId(newId);
    }

    @Override
    public Optional<UserEntity> findById(UUID userId) {
        var sql = "select %s, %s from t_user where %s = ?"
                .formatted(ID_COLUMN, USERNAME_COLUMN, ID_COLUMN);
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRow(rs), userId)
                .stream()
                .findFirst();
    }

    private UserEntity mapRow(ResultSet rs) throws SQLException {
        return new UserEntity()
                .setId(rs.getObject(ID_COLUMN, UUID.class))
                .setUsername(rs.getString(USERNAME_COLUMN));
    }
}
