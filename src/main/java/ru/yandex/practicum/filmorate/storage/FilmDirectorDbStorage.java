package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmDirectorStorage;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmDirector;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class FilmDirectorDbStorage implements FilmDirectorStorage {
    private final JdbcTemplate jdbcTemplate;
    private FilmStorage filmStorage;

    public FilmDirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private FilmDirector makeFilmDirector(ResultSet rs) throws SQLException {
        return new FilmDirector(rs.getLong("film_id"), rs.getInt("director_id"));
    }

    public List<FilmDirector> getFilmDirector(long filmId) {
        String sql = "select * from film_director where film_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilmDirector(rs), filmId);
    }

    public void addFilmDirector(long filmId, int directorId) {
        String sql = "DELETE FROM film_director WHERE film_id = ? ";
        jdbcTemplate.update(sql, filmId);

        String sql1 = "INSERT INTO film_director (FILM_ID, DIRECTOR_ID) VALUES (?, ?)";
        jdbcTemplate.update(sql1, filmId, directorId);
    }

    public void removeFilmDirector(long filmId) {
//        List<FilmDirector> directors = getFilmDirector(filmId);
//        for (FilmDirector director: directors){
//            String sql = "delete from directors where director_id = ?";
//            jdbcTemplate.update(sql, director.getDirectorId());
//        }
        String sql = "delete from film_director where film_id = ?";
        jdbcTemplate.update(sql, filmId);

    }

    protected Set<Director> getListDirectorForFilm(long id) throws NotFoundException {
        if (id <= 0) {
            throw new NotFoundException("ID меньше или равно нулю");
        }
        List<Director> directors = new ArrayList<>();
        SqlRowSet directorRows = jdbcTemplate.queryForRowSet("SELECT d.* FROM films fd " +
                "JOIN directors d ON fd.director_id = d.director_id WHERE film_id = ? ORDER BY director_id ASC", id);
        while (directorRows.next()) {
            Director director = new Director();
            director.setId(directorRows.getInt("director_id"));
            director.setName(directorRows.getString("name_director"));

            directors.add(director);
        }
        Set<Director> newDirectors = new HashSet<>(directors);
        if (newDirectors.isEmpty()) {
            return null;
        }
        return newDirectors;
    }
    @Override
    public Collection<Film> getFilmsByDirector(int directorId, Collection<String> sort) {
        return null;
    }
}