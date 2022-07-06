package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmGenreStorage;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmGenreService {
    private FilmGenreStorage filmGenreStorage;
    private GenreService genreService;

    @Autowired
    public FilmGenreService(FilmGenreStorage filmGenreStorage, GenreService genreService) {
        this.filmGenreStorage = filmGenreStorage;
        this.genreService = genreService;
    }

    Set<Genre> getFilmGenres(long filmId) {
        return filmGenreStorage.getFilmGenres(filmId)
                .stream()
                .map(FilmGenre::getGenreId)
                .map(genreService::getGenre)
                .sorted(Comparator.comparing(Genre::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    void addFilmGenre(long filmId, Set<Genre> genres) {
        genres.forEach((genre) -> filmGenreStorage.addFilmGenre(filmId, genre.getId()));
    }

    void removeFilmGenre(long filmId) {
        filmGenreStorage.removeFilmGenre(filmId);
    }
}