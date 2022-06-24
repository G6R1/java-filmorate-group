package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RateMpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    @Qualifier("FilmDbStorage")
    private FilmStorage filmStorage;
    private UserService userService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public List<RateMpa> getAllMpa() {
        return filmStorage.getAllMpa();
    }

    public RateMpa getMpa(int id) {
        return filmStorage.getRateMpa(id)
                .orElseThrow(() -> new NotFoundException("такого рейтинга не существует"));
    }

    public List<Genre> getAllGenres() {
        return filmStorage.getAllGenres();
    }

    public Genre getGenre(int id) {
        return filmStorage.getGenre(id)
                .orElseThrow(() -> new NotFoundException("такого жанра не существует"));
    }

    public Film getFilm(long filmId) {
        return filmStorage.getFilm(filmId)
                .orElseThrow(() -> new NotFoundException("такого фильма нет в списке"));
    }

    public Film createFilm(Film film) {
        validate(film);
        filmStorage.createFilm(film);
        return film;
    }

    public Film updateFilm(Film film) {
        validate(film);
        getFilm(film.getId());
        filmStorage.updateFilm(film);
        return film;
    }

    public void removeFilm(long filmId) {
        getFilm(filmId);
        filmStorage.removeFilm(filmId);
    }

    public List<Film> getFilms() {
        return new ArrayList<>(filmStorage.getFilms().values());
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
        if (!film.getRateUsers().contains(userId))
            throw new NotFoundException("пользователь не ставил лайков");
        film.removeLike(user.getId());
        filmStorage.updateFilm(film);
        return film;
    }

    public List<Film> getPopular(int count) {
        List<Film> popular = getFilms();
        popular.sort(Film.COMPARE_BY_RATE);
        return popular.stream().limit(count).collect(Collectors.toList());
    }

    private void validate(Film film) {
        if (LocalDate.parse(film.getReleaseDate()).isBefore(LocalDate.of(1895, 12, 28)))
            throw new ValidationException("неправильный фильм");
    }
}