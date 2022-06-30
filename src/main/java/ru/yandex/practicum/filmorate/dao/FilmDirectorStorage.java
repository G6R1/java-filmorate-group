package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmDirector;

import java.util.Collection;
import java.util.List;

public interface FilmDirectorStorage {

    List<FilmDirector> getFilmDirector(long filmId);

    void addFilmDirector(long filmId, int genreId);

    void removeFilmDirector(long filmId);

    Collection <Film> getFilmsByDirector(int directorId, Collection<String> sort);
}
