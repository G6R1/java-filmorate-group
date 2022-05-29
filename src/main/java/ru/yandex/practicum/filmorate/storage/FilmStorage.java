package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {

    //создаёт фильм
    Film createFilm(Film film);

    //возвращает фильм по идентификатору
    Film getFilm(int filmId);

    //обновляет фильм
    Film updateFilm(Film film);

    //удаляет фильм по идентификатору
    void removeFilm(int filmId);

    //возвращает список фильмов
    List<Film> getFilms();
}