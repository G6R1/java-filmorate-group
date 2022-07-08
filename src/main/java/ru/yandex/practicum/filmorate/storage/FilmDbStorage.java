package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class FilmDbStorage implements FilmStorage {
    private MpaService mpaService;
    private GenreService genreService;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(MpaService mpaService, JdbcTemplate jdbcTemplate, GenreService genreService) {
        this.mpaService = mpaService;
        this.jdbcTemplate = jdbcTemplate;
        this.genreService = genreService;
    }

    @Override
    public void createFilm(Film film) {
        String sql = "insert into films(film_name, film_release_date, film_description, film_duration, mpa_id) " +
                "values (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getReleaseDate());
            stmt.setString(3, film.getDescription());
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
    }

    @Override
    public Optional<Film> getFilm(long filmId) {
        String sql = "select* from films join rate_mpa using(mpa_id) where film_id = ?";
        try {
            Film film = jdbcTemplate.queryForObject(sql, this::makeFilm, filmId);
            return Optional.ofNullable(film);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void updateFilm(Film film) {
        String sql = "update films set " +
                "film_name =?, film_release_date =?, film_description =?, film_duration =?, mpa_id =?" +
                "where film_id = ?";
        jdbcTemplate.update(sql
                , film.getName()
                , film.getReleaseDate()
                , film.getDescription()
                , film.getDuration()
                , film.getMpa().getId()
                , film.getId());
    }

    @Override
    public void removeFilm(long filmId) {
        String sqlQuery = "delete from films where id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    @Override
    public Map<Long, Film> getFilms() {
        String sqlQuery = "select* from films join rate_mpa using(mpa_id)";
        return jdbcTemplate.query(sqlQuery, this::makeFilm).stream()
                .collect(Collectors.toMap(Film::getId, item -> item));
    }

    @Override
    public List<Film> getTopFilms(Integer count, Integer genreId, Integer year) throws DataAccessException {
        String sqlQuery = "SELECT f.film_id, film_name, film_release_date, film_description, " +
                "film_duration, mpa_id, fg.genre_id " +
                "FROM films f " +
                "LEFT JOIN (SELECT * FROM film_genres) fg " +
                "ON f.film_id = fg.film_id " +
                "LEFT JOIN (SELECT film_id, COUNT(film_id) count FROM rate_users GROUP BY film_id) ru " +
                "ON f.film_id = ru.film_id " +
                "ORDER BY ru.count DESC " +
                "LIMIT ? ";

        if (genreId != 0 && year != -1) {
        sqlQuery = "SELECT f.film_id, film_name, film_release_date, film_description, \n" +
                "film_duration, mpa_id, fg.genre_id \n" +
                "FROM films f \n" +
                "LEFT JOIN (SELECT * FROM film_genres) fg \n" +
                "ON f.film_id = fg.film_id \n" +
                "LEFT JOIN (SELECT film_id, COUNT(film_id) count FROM rate_users GROUP BY film_id) ru \n" +
                "ON f.film_id = ru.film_id \n" +
                "WHERE (EXTRACT(YEAR from f.film_release_date)) = ? \n" +
                "ORDER BY fg.genre_id DESC \n" +
                "LIMIT 1";
            return jdbcTemplate.query(sqlQuery, this::makeFilmForTop, year);
        } else if(genreId != 0) {
            sqlQuery = "SELECT f.film_id, film_name, film_release_date, film_description, " +
                    "film_duration, mpa_id, fg.genre_id " +
                    "FROM films f " +
                    "LEFT JOIN (SELECT * FROM film_genres) fg " +
                    "ON f.film_id = fg.film_id " +
                    "LEFT JOIN (SELECT film_id, COUNT(film_id) count FROM rate_users GROUP BY film_id) ru " +
                    "ON f.film_id = ru.film_id " +
                    "WHERE fg.genre_id = ? " +
                    "ORDER BY ru.count DESC " +
                    "LIMIT ?";
            return jdbcTemplate.query(sqlQuery, this::makeFilmForTop, genreId, count);
        } else if(year != -1) {
            sqlQuery = "SELECT f.film_id, film_name, film_release_date, film_description, " +
                    "film_duration, mpa_id, fg.genre_id " +
                    "FROM films f " +
                    "LEFT JOIN (SELECT * FROM film_genres) fg " +
                    "ON f.film_id = fg.film_id " +
                    "LEFT JOIN (SELECT film_id, COUNT(film_id) count FROM rate_users GROUP BY film_id) ru " +
                    "ON f.film_id = ru.film_id " +
                    "WHERE (EXTRACT(YEAR from f.film_release_date)) = ? " +
                    "LIMIT 1";
            return jdbcTemplate.query(sqlQuery, this::makeFilmForTop, year);
        }
        return jdbcTemplate.query(sqlQuery,this::makeFilm, count);
    }

    private Film makeFilmForTop(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setName(resultSet.getString("film_name"));
        film.setId(resultSet.getLong("film_id"));
        film.setDescription(resultSet.getString("film_description"));
        film.setReleaseDate(resultSet.getString("film_release_date"));
        film.setDuration(resultSet.getInt("film_duration"));
        film.setGenres(Set.of(genreService.getGenre(resultSet.getInt("genre_id"))));
        film.setMpa(mpaService.getMpa(resultSet.getInt("mpa_id")));
        return film;
    }

    private Film makeFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setName(resultSet.getString("film_name"));
        film.setId(resultSet.getLong("film_id"));
        film.setDescription(resultSet.getString("film_description"));
        film.setReleaseDate(resultSet.getString("film_release_date"));
        film.setDuration(resultSet.getInt("film_duration"));
        film.setMpa(mpaService.getMpa(resultSet.getInt("mpa_id")));
        return film;
    }
}