package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Primary
@Qualifier("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
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
            addFilmGenre(film.getId(), film.getGenres());
        }
        if (film.getRateUsers() != null)
            addRateUsers(film.getId(), film.getRateUsers());
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
        removeFilmGenre(film.getId());
        removeRateUsers(film.getId());
        if (film.getGenres() != null) {
            addFilmGenre(film.getId(), film.getGenres());
            film.setGenres(getFilmGenres(film.getId()));
        }
        if (film.getRateUsers() != null) {
            addRateUsers(film.getId(), film.getRateUsers());
            film.setRateUsers(getRateUsers(film.getId()));
        }
    }

    @Override
    public void removeFilm(long filmId) {
        removeFilmGenre(filmId);
        removeRateUsers(filmId);
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
        if (!getFilmGenres(film.getId()).isEmpty()) {
            film.setGenres(getFilmGenres(film.getId()));
        }
        if (!getRateUsers(film.getId()).isEmpty()) {
            film.setRateUsers(getRateUsers(film.getId()));
        }
        return film;
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

    private Genre makeGenre(ResultSet resultSet, int rowNum) throws SQLException {
        Genre genre = new Genre();
        genre.setId(resultSet.getInt("genre_id"));
        genre.setName(resultSet.getString("genre_name"));
        return genre;
    }

    public Optional<Genre> getGenre(int genreId) {
        try {
            return Optional.ofNullable(jdbcTemplate
                    .queryForObject("select * from genres where genre_id = ?", this::makeGenre, genreId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Genre> getAllGenres() {
        String sqlQuery = "select * from genres";
        return jdbcTemplate.query(sqlQuery, this::makeGenre);
    }

    private FilmGenre makeFilmGenre(ResultSet rs) throws SQLException {
        return new FilmGenre(rs.getLong("film_id"), rs.getInt("genre_id"));
    }

    private Set<Genre> getFilmGenres(long filmId) {
        String sql = "select * from film_genres where film_id = ?";
        List<FilmGenre> genresId = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilmGenre(rs), filmId);
        return genresId.stream()
                .map(FilmGenre::getGenreId)
                .map(this::getGenre)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .sorted(Comparator.comparing(Genre::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private void addFilmGenre(long filmId, Set<Genre> genres) {
        genres.forEach(genre -> {
            String sql = "insert into film_genres(film_id, genre_id) " + "values (?, ?)";
            jdbcTemplate.update(sql, filmId, genre.getId());
        });
    }

    private void removeFilmGenre(long filmId) {
        String sql = "delete from film_genres where film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    private RateUsers makeRateUsers(ResultSet rs) throws SQLException {
        return new RateUsers(rs.getLong("film_id"), rs.getLong("user_id"));
    }

    private Set<Long> getRateUsers(long filmId) {
        String sql = "select * from rate_users where film_id = ?";
        List<RateUsers> rate = jdbcTemplate.query(sql, (rs, rowNum) -> makeRateUsers(rs), filmId);
        return rate.stream().map(RateUsers::getUserId).collect(Collectors.toSet());
    }

    private void addRateUsers(long filmId, Set<Long> userRate) {
        userRate.forEach(rateUser -> {
            String sql = "insert into rate_users(film_id, user_id) " + "values (?, ?)";
            jdbcTemplate.update(sql, filmId, rateUser);
        });
    }

    private void removeRateUsers(long filmId) {
        String sql = "delete from rate_users where film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }
}