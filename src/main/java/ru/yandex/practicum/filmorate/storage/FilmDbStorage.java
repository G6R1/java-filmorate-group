package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RateMpa;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class FilmDbStorage implements FilmStorage {
    private FilmGenreStorage filmGenreStorage;
    private GenreStorage genreStorage;
    private MpaStorage mpaStorage;
    private RateUserStorage rateUserStorage;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(
            FilmGenreStorage filmGenreStorage
            , GenreStorage genreStorage
            , MpaStorage mpaStorage
            , RateUserStorage rateUserStorage
            , JdbcTemplate jdbcTemplate) {
        this.filmGenreStorage = filmGenreStorage;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
        this.rateUserStorage = rateUserStorage;
        this.jdbcTemplate = jdbcTemplate;
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
        if (film.getGenres() != null) {
            filmGenreStorage.addFilmGenre(film.getId(), film.getGenres());
        }
        if (film.getRateUsers() != null)
            rateUserStorage.addRateUsers(film.getId(), film.getRateUsers());
    }

    @Override
    public Optional<Film> getFilm(long filmId) {
        String sql = "select * from films where film_id = ?";
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
        filmGenreStorage.removeFilmGenre(film.getId());
        rateUserStorage.removeRateUsers(film.getId());
        if (film.getGenres() != null) {
            filmGenreStorage.addFilmGenre(film.getId(), film.getGenres());
            film.setGenres(filmGenreStorage.getFilmGenres(film.getId()));
        }
        if (film.getRateUsers() != null) {
            rateUserStorage.addRateUsers(film.getId(), film.getRateUsers());
            film.setRateUsers(rateUserStorage.getRateUsers(film.getId()));
        }
    }

    @Override
    public void removeFilm(long filmId) {
        filmGenreStorage.removeFilmGenre(filmId);
        rateUserStorage.removeRateUsers(filmId);
        String sqlQuery = "delete from films where id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    @Override
    public Map<Long, Film> getFilms() {
        String sqlQuery = "select * from films";
        return jdbcTemplate.query(sqlQuery, this::makeFilm)
                .stream().collect(Collectors.toMap(Film::getId, item -> item));
    }

    private Film makeFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setName(resultSet.getString("film_name"));
        film.setId(resultSet.getLong("film_id"));
        film.setDescription(resultSet.getString("film_description"));
        film.setReleaseDate(resultSet.getString("film_release_date"));
        film.setDuration(resultSet.getInt("film_duration"));
        film.setMpa(getRateMpa(resultSet.getInt("mpa_id")).orElse(new RateMpa()));
        if (!filmGenreStorage.getFilmGenres(film.getId()).isEmpty()) {
            film.setGenres(filmGenreStorage.getFilmGenres(film.getId()));
        }
        if (!rateUserStorage.getRateUsers(film.getId()).isEmpty()) {
            film.setRateUsers(rateUserStorage.getRateUsers(film.getId()));
        }
        return film;
    }

    @Override
    public Optional<Genre> getGenre(int genreId) {
        return genreStorage.getGenre(genreId);
    }

    @Override
    public List<Genre> getAllGenres() {
        return genreStorage.getAllGenres();
    }

    public Optional<RateMpa> getRateMpa(int mpaId) {
        return mpaStorage.getRateMpa(mpaId);
    }

    @Override
    public List<RateMpa> getAllMpa() {
        return mpaStorage.getAllMpa();
    }
}