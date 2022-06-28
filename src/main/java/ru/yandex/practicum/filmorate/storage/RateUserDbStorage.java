package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.RateUserStorage;
import ru.yandex.practicum.filmorate.model.RateUsers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RateUserDbStorage implements RateUserStorage {
    private final JdbcTemplate jdbcTemplate;

    public RateUserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    private RateUsers makeRateUsers(ResultSet rs) throws SQLException {
        return new RateUsers(rs.getLong("film_id"), rs.getLong("user_id"));
    }

    public Set<Long> getRateUsers(long filmId) {
        String sql = "select * from rate_users where film_id = ?";
        List<RateUsers> rate = jdbcTemplate.query(sql, (rs, rowNum) -> makeRateUsers(rs), filmId);
        return rate.stream().map(RateUsers::getUserId).collect(Collectors.toSet());
    }

    public void addRateUser(long filmId, long userId) {
        String sql = "insert into rate_users(film_id, user_id) " + "values (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    public void removeRateUser(long filmId, long userId) {
        String sql = "delete from rate_users where film_id = ? and user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }
}