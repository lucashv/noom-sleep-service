package com.noom.interview.fullstack.sleep.repository.impl;

import com.noom.interview.fullstack.sleep.repository.SleepLogRepository;
import com.noom.interview.fullstack.sleep.repository.entity.SleepLogEntity;
import com.noom.interview.fullstack.sleep.repository.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Repository
@RequiredArgsConstructor
public class SleepLogRepositoryImpl implements SleepLogRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public SleepLogEntity insert(SleepLogEntity entity) throws SQLException {
        var sql = """
                insert into t_sleep_log (user_id, started_at, ended_at, feeling)
                values (?, ?, ?, ? )
                returning id
                """;
        Long id = jdbcTemplate.queryForObject(sql, Long.class,
                entity.getUser().getId(),
                entity.getStartedAt(),
                entity.getEndedAt(),
                entity.getFeeling());
        return entity.setId(id);
    }

    @Override
    public Collection<SleepLogEntity> fetchAll() throws SQLException {
        var sql = "select id, user_id, started_at, ended_at, feeling from t_sleep_log";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRow(rs));
    }

    private SleepLogEntity mapRow(ResultSet rs) throws SQLException {
        var user = new UserEntity().setId(rs.getLong("user_id"));
        return new SleepLogEntity()
                .setId(rs.getLong("id"))
                .setUser(user)
                .setStartedAt(rs.getTimestamp("started_at").toLocalDateTime())
                .setEndedAt(rs.getTimestamp("ended_at").toLocalDateTime())
                .setFeeling(rs.getInt("feeling"));
    }
}
