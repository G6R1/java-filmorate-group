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

    public Collection<Film> getFilmsByDirector(int directorId, Collection<String> sort) {
        List<Film> sortFilmByYear = new ArrayList<>();
        List<Film> sortFilmByLikes = new ArrayList<>();

        if (directorId <= 0 & sort.isEmpty()) {
            throw new NotFoundException("Неверные входные данные");
        }
        if (sort.iterator().next().equals("year")) {
            SqlRowSet yearRows = jdbcTemplate.queryForRowSet("SELECT f.*, d.* FROM films f JOIN film_director fd " +
                    "ON f.film_id = fd.film_id  JOIN directors d ON d.director_id = fd.director_id" +
                    " WHERE fd.director_id = ? ORDER BY FILM_RELEASE_DATE ASC", directorId);
            while (yearRows.next()) {
                Set<Director> directors = new HashSet<>();
                Film film = new Film();
                film.setId(yearRows.getLong("film_id"));
                film.setName(yearRows.getString("film_name"));
                film.setReleaseDate(yearRows.getString("film_release_date"));
                film.setDescription(yearRows.getString("film_description"));
                film.setDuration(yearRows.getInt("film_duration"));
                film.setMpa(mpaService.getMpa(yearRows.getInt("mpa_id")));
                if (filmGenreService.getFilmGenres(yearRows.getLong("film_id")) == null ||
                        filmGenreService.getFilmGenres(yearRows.getLong("film_id")).isEmpty()) {
                    film.setGenres(null);
                } else {
                    film.setGenres(filmGenreService.getFilmGenres(yearRows.getLong("film_id")));
                }
                Director director = new Director();
                director.setId(yearRows.getInt("director_id"));
                director.setName(yearRows.getString("name_director"));
                directors.add(director);
                film.setDirectors(directors);
                sortFilmByYear.add(film);
            }
        } else {
            if (sort.iterator().next().equals("likes")) {
                SqlRowSet likesRows = jdbcTemplate.queryForRowSet("SELECT f.*, d.*, count(USER_ID) as count " +
                        "FROM films f JOIN film_director fd ON f.film_id = fd.film_id" +
                        " JOIN directors d ON d.director_id = fd.director_id" +
                        " LEFT JOIN rate_users ru ON f.FILM_ID = ru.film_id WHERE fd.director_id = ?" +
                        " GROUP BY F.FILM_ID ORDER BY ru.USER_ID ASC", directorId);
                while (likesRows.next()) {
                    Set<Director> directors = new HashSet<>();
                    Film film = new Film();
                    film.setId(likesRows.getLong("film_id"));
                    film.setName(likesRows.getString("film_name"));
                    film.setReleaseDate(likesRows.getString("film_release_date"));
                    film.setDescription(likesRows.getString("film_description"));
                    film.setDuration(likesRows.getInt("film_duration"));
                    film.setMpa(mpaService.getMpa(likesRows.getInt("mpa_id")));
                    if (filmGenreService.getFilmGenres(likesRows.getLong("film_id")) == null ||
                            filmGenreService.getFilmGenres(likesRows.getLong("film_id")).isEmpty()) {
                        film.setGenres(null);
                    } else {
                        film.setGenres(filmGenreService.getFilmGenres(likesRows.getLong("film_id")));
                    }
                    Director director = new Director();
                    director.setId(likesRows.getInt("director_id"));
                    director.setName(likesRows.getString("name_director"));
                    directors.add(director);
                    film.setDirectors(directors);

                    sortFilmByLikes.add(film);
                }
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
}