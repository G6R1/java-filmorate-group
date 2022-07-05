package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmDirectorService;
import ru.yandex.practicum.filmorate.service.FilmGenreService;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class FilmDbStorage implements FilmStorage {
    private MpaService mpaService;
    private FilmDirectorService filmDirectorService;
    private final JdbcTemplate jdbcTemplate;
    private FilmGenreService filmGenreService;

    @Autowired
    public FilmDbStorage(MpaService mpaService, JdbcTemplate jdbcTemplate, FilmDirectorService filmDirectorService,
                         FilmGenreService filmGenreService) {
        this.mpaService = mpaService;
        this.jdbcTemplate = jdbcTemplate;
        this.filmDirectorService = filmDirectorService;
        this.filmGenreService = filmGenreService;
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
        film.setDirectors(filmDirectorService.getFilmDirector(resultSet.getLong("film_id")));
        return film;
    }

    public Collection<Film> getFilmsByDirector(int directorId, Collection<String> sort) {
        List<Film> sortFilmByYear = new ArrayList<>();
        List<Film> sortFilmByLikes = new ArrayList<>();

        if (sort.iterator().next().equals("year")) {
            SqlRowSet yearRows = jdbcTemplate.queryForRowSet("SELECT f.*, d.* FROM films f JOIN film_director fd " +
                    "ON f.film_id = fd.film_id  JOIN directors d ON d.director_id = fd.director_id" +
                    " WHERE fd.director_id = ? ORDER BY FILM_RELEASE_DATE ASC", directorId);
            sortFilmByYear = filmCreator(yearRows);
        } else {
            if (sort.iterator().next().equals("likes")) {
                SqlRowSet likesRows = jdbcTemplate.queryForRowSet("SELECT f.*, d.*, count(USER_ID) as count " +
                        "FROM films f JOIN film_director fd ON f.film_id = fd.film_id" +
                        " JOIN directors d ON d.director_id = fd.director_id" +
                        " LEFT JOIN rate_users ru ON f.FILM_ID = ru.film_id WHERE fd.director_id = ?" +
                        " GROUP BY F.FILM_ID ORDER BY ru.USER_ID ASC", directorId);
                sortFilmByLikes = filmCreator(likesRows);
            }
        }
        if (!sortFilmByLikes.isEmpty()) {
            return sortFilmByLikes;
        } else {
            if (!sortFilmByYear.isEmpty()) {
                return sortFilmByYear;
            } else {
                throw new NotFoundException("Нет данных");
            }
        }
    }

    private List<Film> filmCreator(SqlRowSet filmRows) {
        List<Film> sortFilmByYear = new ArrayList<>();
        while (filmRows.next()) {
            Set<Director> directors = new HashSet<>();
            Film film = new Film();
            film.setId(filmRows.getLong("film_id"));
            film.setName(filmRows.getString("film_name"));
            film.setReleaseDate(filmRows.getString("film_release_date"));
            film.setDescription(filmRows.getString("film_description"));
            film.setDuration(filmRows.getInt("film_duration"));
            film.setMpa(mpaService.getMpa(filmRows.getInt("mpa_id")));
            if (filmGenreService.getFilmGenres(filmRows.getLong("film_id")) == null ||
                    filmGenreService.getFilmGenres(filmRows.getLong("film_id")).isEmpty()) {
                film.setGenres(null);
            } else {
                film.setGenres(filmGenreService.getFilmGenres(filmRows.getLong("film_id")));
            }
            Director director = new Director();
            director.setId(filmRows.getInt("director_id"));
            director.setName(filmRows.getString("name_director"));
            directors.add(director);
            film.setDirectors(directors);
            sortFilmByYear.add(film);
        }
        return sortFilmByYear;
    }
}