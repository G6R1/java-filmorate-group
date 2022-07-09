package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.DirectorStorage;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createDirector(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        simpleJdbcInsert.withTableName("directors").usingGeneratedKeyColumns("director_id");
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("director_name", director.getName());
        Number num = simpleJdbcInsert.executeAndReturnKey(parameters);
        director.setId(num.intValue());
    }

    public void updateDirector(Director director) {
        String sql = "update directors set " +
                "director_name = ? where director_id = ?";
        jdbcTemplate.update(sql, director.getName(), director.getId());
    }

    public void removeDirector(int directorId) {
        String sql = "delete from directors where director_id = ?";
        jdbcTemplate.update(sql, directorId);
    }

    public Optional<Director> getDirector(int id) {
        try {
            return Optional.ofNullable(jdbcTemplate
                    .queryForObject("SELECT * FROM directors WHERE director_id = ?", this::makeDirector, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Director> getListDirectors() {
        String sqlQuery = "select * from directors";
        return jdbcTemplate.query(sqlQuery, this::makeDirector);
    }

    private Director makeDirector(ResultSet resultSet, int rowNum) throws SQLException {
        Director director = new Director();
        director.setId(resultSet.getInt("director_id"));
        director.setName(resultSet.getString("director_name"));
        return director;
    }
}