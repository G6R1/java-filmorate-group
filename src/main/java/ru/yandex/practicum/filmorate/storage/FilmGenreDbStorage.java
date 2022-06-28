package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmGenreStorage;
import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class FilmGenreDbStorage implements FilmGenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmGenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private FilmGenre makeFilmGenre(ResultSet rs) throws SQLException {
        return new FilmGenre(rs.getLong("film_id"), rs.getInt("genre_id"));
    }

    public List<FilmGenre> getFilmGenres(long filmId) {
        String sql = "select * from film_genres where film_id = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilmGenre(rs), filmId);

    }

    public void addFilmGenre(long filmId, int genreId) {
        String sql = "insert into film_genres(film_id, genre_id) " + "values (?, ?)";
        jdbcTemplate.update(sql, filmId, genreId);
    }

    public void removeFilmGenre(long filmId) {
        String sql = "delete from film_genres where film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }
}