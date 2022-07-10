package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.SortType;

import java.util.*;

public interface FilmStorage {

    Collection<Film> getCommon(long userId, long friendId);

    //создаёт фильм
    void createFilm(Film film);

    //возвращает фильм по идентификатору
    Optional<Film> getFilm(long filmId);

    //обновляет фильм
    void updateFilm(Film film);

    //удаляет фильм по идентификатору
    void removeFilm(long filmId);

    //возвращает список фильмов
    Collection<Film> getFilms();

    Collection<Film> getFilmsByDirector(int directorId, SortType sortBy);

    Collection<Film> getFilmsSearch(String query, EnumSet<SortType> sortBy);

    Collection<Film> getFilmsPopular(int count, Integer genre, String year);
}
