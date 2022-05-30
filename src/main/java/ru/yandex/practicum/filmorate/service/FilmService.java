package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class FilmService {
    private FilmStorage filmStorage;
    private UserService userService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Film getFilm(long filmId) {
        return filmStorage.getFilm(filmId)
                .orElseThrow(() -> new NotFoundException("такого фильма нет в списке"));
    }

    public Film createFilm(Film film) {
        if (validate(film) || filmStorage.getFilms().containsKey(film.getId()))
            throw new ValidationException("неправильный фильм");
        filmStorage.createFilm(film);
        return film;
    }

    public Film updateFilm(Film film) {
        if (validate(film) || !filmStorage.getFilms().containsKey(film.getId()))
            throw new NotFoundException("такого фильма нет в списке");
        getFilm(film.getId());
        filmStorage.updateFilm(film);
        return film;
    }

    public void removeFilm(long filmId) {
        getFilm(filmId);
        filmStorage.removeFilm(filmId);
    }

    public List<Film> getFilms() {
        List<Film> filmList = new ArrayList<>(0);
        for (Long key : filmStorage.getFilms().keySet()) {
            Film film = filmStorage.getFilms().get(key);
            filmList.add(film);
        }
        return filmList;
    }

    public Film addLike(long filmId, long userId) {
        Film film = getFilm(filmId);
        User user = userService.getUser(userId);
        film.addLike(user.getId());
        filmStorage.updateFilm(film);
        return film;
    }

    public Film removeLike(long filmId, long userId) {
        User user = userService.getUser(userId);
        Film film = getFilm(filmId);
        if (film.getLikes().isEmpty()) {
            throw new NotFoundException("отсутствует список лайков");
        } else {
            film.removeLike(user.getId());
        }
        return film;
    }

    public List<Film> getPopular(int count) {
        List<Film> popular;
        popular = getFilms();
        popular.sort(Film.COMPARE_BY_RATE);
        if (count < popular.size()) {
            return popular.subList(0, count);
        } else
            return popular;
    }

    private boolean validate(Film film) {
        return LocalDate.parse(film.getReleaseDate()).isBefore(LocalDate.of(1895, 12, 28));
    }
}