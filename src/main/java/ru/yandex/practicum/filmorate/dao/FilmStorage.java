package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
<<<<<<< HEAD
=======
import java.util.List;
>>>>>>> add-common-films
import java.util.Map;
import java.util.Optional;

public interface FilmStorage {

    List<Film> getCommon(int userId, int friendId);

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

<<<<<<< HEAD
=======

>>>>>>> add-common-films
    Collection<Film> getFilmsSearch(String query, String sortBy);
}