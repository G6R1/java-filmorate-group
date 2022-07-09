package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.FilmDirector;

import java.util.List;
import java.util.Set;

public interface FilmDirectorStorage {

    void addFilmDirector(long filmId, int genreId);

    void removeFilmDirector(long filmId);

    Set<Director> getDirectorFromFilm(long filmId);
}
