package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.DirectorStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@Qualifier("directorDbStorage")
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Director createDirector(Director director) {

        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("directors").usingGeneratedKeyColumns("director_id");
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("name_director", director.getName());
        Number num = jdbcInsert.executeAndReturnKey(parameters);
        director.setId(num.intValue());
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        if (director.getId() <= 0 & director.getName() != null) {
            throw new NotFoundException("id не может быть меньше 0");
        }
        Director director1 = new Director();
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("SELECT * FROM directors WHERE director_id = ?",
                director.getId());
        while (genreRows.next()) {
            director1.setId(genreRows.getInt("director_id"));
            director1.setName(genreRows.getString("name_director"));
        }
        if (director1.getId() == 0) {
            throw new NotFoundException("Режиссер с ID# " + director.getId() + " не найден");
        } else {
            String sql = "UPDATE directors set " +
                    "name_director =? WHERE DIRECTOR_ID = ?";
            jdbcTemplate.update(sql, director.getName(), director.getId());
        }
        return director;
    }

    @Override
    public void removeDirector(int id) {
        if (id <= 0) {
            throw new NotFoundException("ID меньше или равно 0");
        }
        String sql2 = "DELETE FROM film_director WHERE director_id = ?";
        jdbcTemplate.update(sql2, id);
        String sql = "DELETE FROM directors WHERE director_id = ?";
        jdbcTemplate.update(sql, id);

    }

    @Override
    public Optional<Director> getDirector(int directorId) {
        try {
            return Optional.ofNullable(jdbcTemplate
                    .queryForObject("SELECT * FROM directors WHERE director_id = ?", this::makeDirector, directorId));
        } catch (EmptyResultDataAccessException e) {
            log.info("Режиссер не найден");
            return Optional.empty();
        }
    }

    @Override
    public List<Director> getListDirectors() {
        String sqlQuery = "SELECT * FROM directors ORDER BY director_id ASC";
        return jdbcTemplate.query(sqlQuery, this::makeDirector);
    }

    private Director makeDirector(ResultSet resultSet, int rowNum) throws SQLException {
        Director director = new Director();
        director.setId(resultSet.getInt("director_id"));
        director.setName(resultSet.getString("name_director"));
        return director;
    }
}
