package com.noom.interview.fullstack.sleep.repository.impl;

import com.noom.interview.fullstack.sleep.repository.SleepLogRepository;
import com.noom.interview.fullstack.sleep.repository.entity.SleepLogEntity;
import com.noom.interview.fullstack.sleep.repository.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SleepLogRepositoryImpl implements SleepLogRepository {

    private static final String ID_COLUMN = "id";
    private static final String USER_ID_COLUMN = "user_id";
    private static final String SLEEP_DATE_COLUMN = "sleep_date";
    private static final String STARTED_AT_COLUMN = "started_at";
    private static final String ENDED_AT_COLUMN = "ended_at";
    private static final String FEELING_COLUMN = "feeling";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public SleepLogEntity insert(SleepLogEntity entity) {
        var sql = """
                insert into t_sleep_log (%s, %s, %s, %s, %s, %s)
                values (?, ?, ?, ?, ?, ?)
                """.formatted(ID_COLUMN, USER_ID_COLUMN, SLEEP_DATE_COLUMN, STARTED_AT_COLUMN,
                ENDED_AT_COLUMN, FEELING_COLUMN);
        var newId = UUID.randomUUID();
        jdbcTemplate.update(sql,
                newId,
                entity.getUser().getId(),
                entity.getSleepDate(),
                entity.getStartedAt(),
                entity.getEndedAt(),
                entity.getFeeling());
        return entity.setId(newId);
    }

    @Override
    public List<SleepLogEntity> filterByUserAndSleepDateRange(
            UUID userId, LocalDate from, LocalDate to) {
        var sql = "select %s, %s, %s, %s, %s, %s from t_sleep_log where %s = ? and %s between ? and ?"
                .formatted(ID_COLUMN, USER_ID_COLUMN, SLEEP_DATE_COLUMN, STARTED_AT_COLUMN,
                        ENDED_AT_COLUMN, FEELING_COLUMN, USER_ID_COLUMN, SLEEP_DATE_COLUMN);
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRow(rs), userId, from, to);
    }

    private SleepLogEntity mapRow(ResultSet rs) throws SQLException {
        var user = new UserEntity().setId(rs.getObject(USER_ID_COLUMN, UUID.class));

        var startedAt = rs.getTimestamp(STARTED_AT_COLUMN);
        var endedAt = rs.getTimestamp(ENDED_AT_COLUMN);

        return new SleepLogEntity()
                .setId(rs.getObject(ID_COLUMN, UUID.class))
                .setUser(user)
                .setSleepDate(rs.getDate(SLEEP_DATE_COLUMN).toLocalDate())
                .setStartedAt(startedAt != null ? startedAt.toLocalDateTime() : null)
                .setEndedAt(endedAt != null ? endedAt.toLocalDateTime() : null)
                .setFeeling(rs.getString(FEELING_COLUMN));
    }
}
