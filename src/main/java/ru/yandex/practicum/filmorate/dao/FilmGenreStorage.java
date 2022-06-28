package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.util.List;

public interface FilmGenreStorage {

    List<FilmGenre> getFilmGenres(long filmId);

    void addFilmGenre(long filmId, int genreId);

    void removeFilmGenre(long filmId);
}