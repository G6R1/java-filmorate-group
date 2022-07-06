package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FilmStorage {

    List<Film> getCommon(long userId, long friendId);

    //создаёт фильм
    void createFilm(Film film);

    //возвращает фильм по идентификатору
    Optional<Film> getFilm(long filmId);

    //обновляет фильм
    void updateFilm(Film film);

    //удаляет фильм по идентификатору
    void removeFilm(long filmId);

    //возвращает список фильмов
    Map<Long, Film> getFilms();

    Collection<Film> getFilmsByDirector(int directorId, Collection<String> sortBy);

    Collection<Film> getFilmsSearch(String query, String sortBy);
}