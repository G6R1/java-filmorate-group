package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmDirectorStorage;
import ru.yandex.practicum.filmorate.model.FilmDirector;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class FilmDirectorDbStorage implements FilmDirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private FilmDirector makeFilmDirector(ResultSet rs) throws SQLException {
        return new FilmDirector(rs.getLong("film_id"), rs.getInt("director_id"));
    }

    public List<FilmDirector> getFilmDirectors(long filmId) {
        String sql = "select * from film_director where film_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilmDirector(rs), filmId);
    }

    public void addFilmDirector(long filmId, int directorId) {
        String sql = "insert into film_director (film_id, director_id) values (?, ?)";
        jdbcTemplate.update(sql, filmId, directorId);
    }

    public void removeFilmDirector(long filmId) {
        String sql = "delete from film_director where film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }
}