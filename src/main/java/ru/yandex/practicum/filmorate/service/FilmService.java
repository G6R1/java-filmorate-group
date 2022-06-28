package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private FilmGenreService filmGenreService;
    private FilmStorage filmStorage;
    private RateUserService rateUserService;
    private UserService userService;

    @Autowired
    public FilmService(
            FilmGenreService filmGenreService
            , FilmStorage filmStorage
            , RateUserService rateUserService
            , UserService userService) {
        this.filmGenreService = filmGenreService;
        this.filmStorage = filmStorage;
        this.rateUserService = rateUserService;
        this.userService = userService;
    }

    public Film getFilm(long filmId) {
        Film film = filmStorage.getFilm(filmId).orElseThrow(() -> new NotFoundException("такого фильма нет в списке"));
        if (!filmGenreService.getFilmGenres(film.getId()).isEmpty()) {
            film.setGenres(filmGenreService.getFilmGenres(film.getId()));
        }
        if (!rateUserService.getRateUsers(film.getId()).isEmpty()) {
            film.setRateUsers(rateUserService.getRateUsers(film.getId()).size());
        }
        return film;
    }

    public Film createFilm(Film film) {
        validate(film);
        filmStorage.createFilm(film);
        if (film.getGenres() != null) {
            filmGenreService.addFilmGenre(film.getId(), film.getGenres());
            film.setGenres(filmGenreService.getFilmGenres(film.getId()));
        }
        return film;
    }

    public Film updateFilm(Film film) {
        validate(film);
        getFilm(film.getId());
        filmGenreService.removeFilmGenre(film.getId());
        if (film.getGenres() != null) {
            filmGenreService.addFilmGenre(film.getId(), film.getGenres());
            film.setGenres(filmGenreService.getFilmGenres(film.getId()));
        }
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
        rateUserService.addRateUser(film.getId(), user.getId());
        filmStorage.updateFilm(film);
        return getFilm(filmId);
    }

    public Film removeLike(long filmId, long userId) {
        User user = userService.getUser(userId);
        Film film = getFilm(filmId);
        if (!rateUserService.getRateUsers(filmId).contains(userId))
            throw new NotFoundException("пользователь не ставил лайков");
        rateUserService.removeRateUser(film.getId(), user.getId());
        filmStorage.updateFilm(film);
        return getFilm(filmId);
    }

    public List<Film> getPopular(int count) {
        List<Film> films = getFilms();
        List<Film> popular = new ArrayList<>();
        films.forEach(film -> popular.add(getFilm(film.getId())));
        popular.sort(Film.COMPARE_BY_RATE);
        return popular.stream().limit(count).collect(Collectors.toList());
    }

    private void validate(Film film) {
        if (LocalDate.parse(film.getReleaseDate()).isBefore(LocalDate.of(1895, 12, 28)))
            throw new ValidationException("неправильный фильм");
    }
}
