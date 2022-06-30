package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import javax.xml.crypto.Data;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FilmStorage {

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

    //возвращает список фильмов с режиссером и сортировкой по году и количеству лайков
    List<Film> getListFilmsByDirector(int id, Map<Data, String> sortBy);
}