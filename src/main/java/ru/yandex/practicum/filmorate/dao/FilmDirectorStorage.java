package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.FilmDirector;

import java.util.List;

public interface FilmDirectorStorage {

    List<FilmDirector> getFilmDirectors(long filmId);

    void addFilmDirector(long filmId, int genreId);

    void removeFilmDirector(long filmId);
}
