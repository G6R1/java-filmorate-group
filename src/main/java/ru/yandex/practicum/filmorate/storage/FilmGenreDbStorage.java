package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmGenreStorage;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FilmGenreDbStorage implements FilmGenreStorage {
    private GenreStorage genreStorage;
    private final JdbcTemplate jdbcTemplate;

    public FilmGenreDbStorage(GenreStorage genreStorage, JdbcTemplate jdbcTemplate) {
        this.genreStorage = genreStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    private FilmGenre makeFilmGenre(ResultSet rs) throws SQLException {
        return new FilmGenre(rs.getLong("film_id"), rs.getInt("genre_id"));
    }

    public Set<Genre> getFilmGenres(long filmId) {
        String sql = "select * from film_genres where film_id = ?";
        List<FilmGenre> genresId = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilmGenre(rs), filmId);
        return genresId.stream()
                .map(FilmGenre::getGenreId)
                .map(genreStorage::getGenre)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .sorted(Comparator.comparing(Genre::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public void addFilmGenre(long filmId, Set<Genre> genres) {
        genres.forEach(genre -> {
            String sql = "insert into film_genres(film_id, genre_id) " + "values (?, ?)";
            jdbcTemplate.update(sql, filmId, genre.getId());
        });
    }

    public void removeFilmGenre(long filmId) {
        String sql = "delete from film_genres where film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }
}