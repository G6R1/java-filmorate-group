package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RateMpa;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FilmStorage {

    List<RateMpa> getAllMpa();

    Optional<RateMpa> getRateMpa(int mpaId);

    List<Genre> getAllGenres();

    Optional<Genre> getGenre(int genreId);

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
