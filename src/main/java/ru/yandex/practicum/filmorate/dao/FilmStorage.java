package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FilmStorage {

    //возвращает общие фильмы
    public List<Film> getCommon(int userId, int friendId);

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
}