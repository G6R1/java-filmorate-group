package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmDirectorStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmDirector;
import ru.yandex.practicum.filmorate.service.FilmGenreService;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class FilmDirectorDbStorage implements FilmDirectorStorage {
    private final JdbcTemplate jdbcTemplate;
    private MpaService mpaService;
    private FilmGenreService filmGenreService;

    public FilmDirectorDbStorage(JdbcTemplate jdbcTemplate, MpaService mpaService, FilmGenreService filmGenreService) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaService = mpaService;
        this.filmGenreService = filmGenreService;
    }

    private FilmDirector makeFilmDirector(ResultSet rs) throws SQLException {
        return new FilmDirector(rs.getLong("film_id"), rs.getInt("director_id"));
    }

    public List<FilmDirector> getFilmDirector(long filmId) {
        String sql = "select * from film_director where film_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilmDirector(rs), filmId);
    }

    public void addFilmDirector(long filmId, int directorId) {
        String sql1 = "INSERT INTO film_director(FILM_ID, DIRECTOR_ID) " + "VALUES (?, ?)";
        jdbcTemplate.update(sql1, filmId, directorId);
    }

    public void removeFilmDirector(long filmId) {
        String sql = "delete from film_director where film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }
}