package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Set;

public interface FilmGenreStorage {

    Set<Genre> getFilmGenres(long filmId);

    void addFilmGenre(long filmId, Set<Genre> genres);

    void removeFilmGenre(long filmId);
}