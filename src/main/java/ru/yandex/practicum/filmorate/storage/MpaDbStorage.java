package ru.yandex.practicum.filmorate.storage;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.MpaStorage;
import ru.yandex.practicum.filmorate.model.RateMpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    private RateMpa makeMpa(ResultSet resultSet, int rowNum) throws SQLException {
        RateMpa rateMpa = new RateMpa();
        rateMpa.setId(resultSet.getInt("mpa_id"));
        rateMpa.setName(resultSet.getString("mpa_name"));
        rateMpa.setMpaDescription(resultSet.getString("mpa_description"));
        return rateMpa;
    }

    public Optional<RateMpa> getRateMpa(int mpaId) {
        try {
            return Optional.ofNullable(jdbcTemplate
                    .queryForObject("select * from rate_mpa where mpa_id = ?", this::makeMpa, mpaId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<RateMpa> getAllMpa() {
        String sqlQuery = "select * from rate_mpa";
        return jdbcTemplate.query(sqlQuery, this::makeMpa);
    }
}