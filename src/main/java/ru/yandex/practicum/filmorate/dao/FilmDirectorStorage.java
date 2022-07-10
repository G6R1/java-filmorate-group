package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Set;

public interface FilmDirectorStorage {

    Set<Director> getFilmDirectors(long filmId);

    void addFilmDirector(long filmId, int genreId);

    void removeFilmDirector(long filmId);
}