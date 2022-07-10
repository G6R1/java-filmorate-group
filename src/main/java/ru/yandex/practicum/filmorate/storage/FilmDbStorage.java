package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.SortType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Film> getCommon(long userId, long friendId) {
        String sql = "SELECT " +
                "f.FILM_ID, f.FILM_NAME, f.FILM_DESCRIPTION, f.FILM_DURATION, f.FILM_RELEASE_DATE, f.MPA_ID," +
                " count(l.USER_ID) AS count_films " +
                "FROM films AS f " +
                "LEFT JOIN RATE_USERS AS l ON f.FILM_ID = l.FILM_ID " +
                "WHERE l.USER_ID = ? and f.FILM_ID in " +
                "(select films.FILM_ID from FILMS, RATE_USERS where films.film_id = RATE_USERS.film_id " +
                "and RATE_USERS.user_id = ?) " +
                "GROUP BY f.film_id, f.FILM_NAME, f.FILM_DESCRIPTION, f.FILM_DURATION, f.FILM_RELEASE_DATE, f.MPA_ID " +
                "ORDER BY count_films desc ";
        return jdbcTemplate.query(sql, this::makeFilm, userId, friendId);
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
    public Collection<Film> getFilms() {
        String sqlQuery = "select* from films join rate_mpa using(mpa_id)";
        return jdbcTemplate.query(sqlQuery, this::makeFilm);
    }

    private Film makeFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setName(resultSet.getString("film_name"));
        film.setId(resultSet.getLong("film_id"));
        film.setDescription(resultSet.getString("film_description"));
        film.setReleaseDate(resultSet.getString("film_release_date"));
        film.setDuration(resultSet.getInt("film_duration"));
        return film;
    }

    public Collection<Film> getFilmsByDirector(int directorId, SortType sort) {
        List<Film> sortFilm = new ArrayList<>();
        if (sort.name().equals("year")) {
            String sqlQuery = "SELECT f.*, d.* FROM films f JOIN film_director fd " +
                    "ON f.film_id = fd.film_id  JOIN directors d ON d.director_id = fd.director_id" +
                    " WHERE fd.director_id = ? ORDER BY FILM_RELEASE_DATE ASC;";
            sortFilm = jdbcTemplate.query(sqlQuery, this::makeFilm, directorId);
        }
        if (sort.name().equals("likes")) {
            String sqlQuery = "SELECT f.*, d.*, count(USER_ID) as count " +
                    "FROM films f JOIN film_director fd ON f.film_id = fd.film_id" +
                    " JOIN directors d ON d.director_id = fd.director_id" +
                    " LEFT JOIN rate_users ru ON f.FILM_ID = ru.film_id WHERE fd.director_id = ?" +
                    " GROUP BY F.FILM_ID ORDER BY ru.USER_ID ASC;";
            sortFilm = jdbcTemplate.query(sqlQuery, this::makeFilm, directorId);
        }
        return sortFilm;
    }

    public Collection<Film> getFilmsSearch(String query, EnumSet<SortType> sortBy) {
        List<Film> sortFilm = new ArrayList<>();
        if (sortBy.equals(EnumSet.of(SortType.title, SortType.director))) {
            String sqlQuery = "select* " +
                    "FROM films f LEFT JOIN film_director fd ON f.film_id = fd.film_id" +
                    " LEFT JOIN directors d ON d.director_id = fd.director_id" +
                    " LEFT JOIN rate_users ru ON f.film_id = ru.film_id" +
                    " where lower(d.director_name) like lower('%" + query + "%') or" +
                    " lower(f.film_name) like lower('%" + query + "%') " +
                    " GROUP BY f.film_id ORDER BY count(ru.user_id) desc;";
            sortFilm = jdbcTemplate.query(sqlQuery, this::makeFilm);
        }
        if (sortBy.equals(EnumSet.of(SortType.title))) {
            String sqlQuery = "select* " +
                    "FROM films f LEFT JOIN rate_users ru ON f.film_id = ru.film_id" +
                    " where lower(f.film_name) like lower('%" + query + "%')" +
                    " GROUP BY f.film_id ORDER BY count(ru.user_id) desc;";
            sortFilm = jdbcTemplate.query(sqlQuery, this::makeFilm);
        }
        if (sortBy.equals(EnumSet.of(SortType.director))) {
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

    public Collection<Film> getFilmsPopular(int count, Integer genre, String year) {
        List<Film> sortFilm = new ArrayList<>();
        if (genre != null && year != null) {
            String sqlQuery = "select f.*, fg.* FROM films f " +
                    "JOIN rate_mpa r on f.mpa_id=r.mpa_id " +
                    "JOIN film_genres fg ON f.film_id = fg.film_id " +
                    "LEFT JOIN rate_users ru ON f.film_id = ru.film_id " +
                    "WHERE fg.genre_id = ? and f.film_release_date like ('%" + year + "%') " +
                    "GROUP BY f.film_id ORDER BY count(ru.user_id) desc LIMIT ?;";
            sortFilm = jdbcTemplate.query(sqlQuery, this::makeFilm, genre, count);
        }
        if (genre != null && year == null) {
            String sqlQuery = "select f.*, r.* FROM films f " +
                    "JOIN rate_mpa r on f.mpa_id=r.mpa_id " +
                    "JOIN film_genres fg ON f.film_id = fg.film_id " +
                    "LEFT JOIN rate_users ru ON f.film_id = ru.film_id " +
                    "WHERE fg.genre_id = ? " +
                    "GROUP BY f.film_id ORDER BY count(ru.user_id) desc LIMIT ?;";
            sortFilm = jdbcTemplate.query(sqlQuery, this::makeFilm, genre, count);
        }
        if (year != null && genre == null) {
            String sqlQuery = "select f.*, r.* FROM films f " +
                    "JOIN rate_mpa r on f.mpa_id=r.mpa_id " +
                    "LEFT JOIN rate_users ru ON f.film_id = ru.film_id " +
                    "WHERE f.film_release_date like ('%" + year + "%') " +
                    "GROUP BY f.film_id ORDER BY count(ru.user_id) desc LIMIT ?;";
            sortFilm = jdbcTemplate.query(sqlQuery, this::makeFilm, count);
        }
        if (year == null && genre == null) {
            String sqlQuery = "select f.*, r.* FROM films f " +
                    "JOIN rate_mpa r on f.mpa_id=r.mpa_id " +
                    "LEFT JOIN rate_users ru ON f.film_id = ru.film_id " +
                    "GROUP BY f.film_id ORDER BY count(ru.user_id) desc LIMIT ?;";
            sortFilm = jdbcTemplate.query(sqlQuery, this::makeFilm, count);
        }
        return sortFilm;
    }
}