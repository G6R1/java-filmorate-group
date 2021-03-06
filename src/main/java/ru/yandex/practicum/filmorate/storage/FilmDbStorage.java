package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmDirectorService;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class FilmDbStorage implements FilmStorage {
    private FilmDirectorService filmDirectorService;
    private MpaService mpaService;
    private final JdbcTemplate jdbcTemplate;

    @Autowired

    public FilmDbStorage(FilmDirectorService filmDirectorService,
                         MpaService mpaService,
                         JdbcTemplate jdbcTemplate) {
        this.filmDirectorService = filmDirectorService;
        this.mpaService = mpaService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> getCommon(long userId, long friendId) {
        String sql = "SELECT f.FILM_ID, f.FILM_NAME, f.FILM_DESCRIPTION, f.FILM_DURATION, f.FILM_RELEASE_DATE, f.MPA_ID, count(l.USER_ID) AS count_films " +
                "FROM films AS f " +
                "LEFT JOIN RATE_USERS AS l ON f.FILM_ID = l.FILM_ID " +
                "WHERE l.USER_ID = ? and f.FILM_ID in " +
                "(select films.FILM_ID from FILMS, RATE_USERS where films.film_id = RATE_USERS.film_id and RATE_USERS.user_id = ?) " +
                "GROUP BY f.film_id, f.FILM_NAME, f.FILM_DESCRIPTION, f.FILM_DURATION, f.FILM_RELEASE_DATE, f.MPA_ID " +
                "ORDER BY count_films desc ";
        return jdbcTemplate.query(sql, this::makeFilm);
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
        String sqlQuery = "DELETE FROM FILMS where FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, filmId);

    }

    @Override
    public Map<Long, Film> getFilms() {
        String sqlQuery = "select* from films join rate_mpa using(mpa_id)";
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
        film.setMpa(mpaService.getMpa(resultSet.getInt("mpa_id")));
        film.setDirectors(filmDirectorService.getFilmDirectors(resultSet.getLong("film_id")));
        return film;
    }

    public Collection<Film> getFilmsByDirector(int directorId, Collection<String> sort) {
        List<Film> sortFilm = new ArrayList<>();
        if (sort.iterator().next().equals("year")) {
            String sqlQuery = "SELECT f.*, d.* FROM films f JOIN film_director fd " +
                    "ON f.film_id = fd.film_id  JOIN directors d ON d.director_id = fd.director_id" +
                    " WHERE fd.director_id = ? ORDER BY FILM_RELEASE_DATE ASC;";
            sortFilm = jdbcTemplate.query(sqlQuery, this::makeFilm, directorId);
        }
        if (sort.iterator().next().equals("likes")) {
            String sqlQuery = "SELECT f.*, d.*, count(USER_ID) as count " +
                    "FROM films f JOIN film_director fd ON f.film_id = fd.film_id" +
                    " JOIN directors d ON d.director_id = fd.director_id" +
                    " LEFT JOIN rate_users ru ON f.FILM_ID = ru.film_id WHERE fd.director_id = ?" +
                    " GROUP BY F.FILM_ID ORDER BY ru.USER_ID ASC;";
            sortFilm = jdbcTemplate.query(sqlQuery, this::makeFilm, directorId);
        }
        return sortFilm;
    }

    public Collection<Film> getFilmsSearch(String query, String sortBy) {
        List<Film> sortFilm = new ArrayList<>();
        if (sortBy.equals("title,director")) {
            String sqlQuery = "select* " +
                    "FROM films f LEFT JOIN film_director fd ON f.film_id = fd.film_id" +
                    " LEFT JOIN directors d ON d.director_id = fd.director_id" +
                    " LEFT JOIN rate_users ru ON f.film_id = ru.film_id" +
                    " where lower(d.director_name) like lower('%" + query + "%') or" +
                    " lower(f.film_name) like lower('%" + query + "%') " +
                    " GROUP BY f.film_id ORDER BY count(ru.user_id) desc;";
            sortFilm = jdbcTemplate.query(sqlQuery, this::makeFilm);
        }
        if (sortBy.equals("title")) {
            String sqlQuery = "select* " +
                    "FROM films f LEFT JOIN rate_users ru ON f.film_id = ru.film_id" +
                    " where lower(f.film_name) like lower('%" + query + "%')" +
                    " GROUP BY f.film_id ORDER BY count(ru.user_id) desc;";
            sortFilm = jdbcTemplate.query(sqlQuery, this::makeFilm);
        }
        if (sortBy.equals("director")) {
            String sqlQuery = "select* " +
                    "FROM films f LEFT JOIN film_director fd ON f.film_id = fd.film_id" +
                    " LEFT JOIN directors d ON d.director_id = fd.director_id" +
                    " LEFT JOIN rate_users ru ON f.film_id = ru.film_id" +
                    " where lower(d.director_name) like lower('%" + query + "%')" +
                    " GROUP BY f.film_id ORDER BY count(ru.user_id) desc;";
            sortFilm = jdbcTemplate.query(sqlQuery, this::makeFilm);
        }
        return sortFilm;
    }
}