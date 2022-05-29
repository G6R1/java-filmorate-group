package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.InternalErrorException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
public class FilmService {
    private FilmStorage filmStorage;
    private UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addLike(int filmId, int userId) {
        Film film = filmStorage.getFilm(filmId);
        User user = userStorage.getUser(userId);
        film.addLike(user.getId());
        filmStorage.updateFilm(film);
        return film;
    }

    public Film removeLike(int filmId, int userId) {
        User user = userStorage.getUser(userId);
        Film film = filmStorage.getFilm(filmId);
        if (film.getLikes() == null) {
            throw new InternalErrorException("отсутствует список лайков");
        } else {
            film.removeLike(user.getId());
        }
        return film;
    }

    public List<Film> getPopular(int count) {
        List<Film> popular;
        if (count < 0) {
            throw new ValidationException("запрос не соответствует количеству фильмов");
        } else {
            popular = filmStorage.getFilms();
            popular.sort(Film.COMPARE_BY_RATE);
        }
        if (count < popular.size()) {
            return popular.subList(0, count);
        } else
            return popular;
    }
}