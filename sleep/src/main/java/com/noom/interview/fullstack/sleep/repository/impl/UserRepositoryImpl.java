package com.noom.interview.fullstack.sleep.repository.impl;

import com.noom.interview.fullstack.sleep.repository.UserRepository;
import com.noom.interview.fullstack.sleep.repository.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public UserEntity insert(UserEntity entity) throws SQLException {
        var sql = "insert into t_user (username) values (?) returning id";
        Long id = jdbcTemplate.queryForObject(sql, Long.class, entity.getUsername());
        return entity.setId(id);
    }

    @Override
    public Collection<UserEntity> fetchAll() throws SQLException {
        var sql = "select id, username from t_user";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRow(rs));
    }

    private UserEntity mapRow(ResultSet rs) throws SQLException {
        return new UserEntity()
                .setId(rs.getLong("id"))
                .setUsername(rs.getString("username"));
    }
}
