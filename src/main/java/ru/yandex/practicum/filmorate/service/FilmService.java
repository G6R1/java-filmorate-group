package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDirectorStorage;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private FilmGenreService filmGenreService;
    private FilmStorage filmStorage;
    private RateUserService rateUserService;
    private UserService userService;
    private FilmDirectorService filmDirectorService;

    private FilmDirectorStorage filmDirectorStorage;

    JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmService(
            FilmGenreService filmGenreService
            , FilmStorage filmStorage
            , RateUserService rateUserService
            , UserService userService, JdbcTemplate jdbcTemplate
            , FilmDirectorService filmDirectorService, FilmDirectorStorage filmDirectorStorage) {
        this.filmGenreService = filmGenreService;
        this.filmStorage = filmStorage;
        this.rateUserService = rateUserService;
        this.userService = userService;
        this.jdbcTemplate = jdbcTemplate;
        this.filmDirectorService = filmDirectorService;
        this.filmDirectorStorage = filmDirectorStorage;

    }

    public Film getFilm(long filmId) {
        Film film = filmStorage.getFilm(filmId).orElseThrow(() -> new NotFoundException("такого фильма нет в списке"));
        if (!filmGenreService.getFilmGenres(film.getId()).isEmpty()) {
            film.setGenres(filmGenreService.getFilmGenres(film.getId()));
        } else {
            film.setGenres(null);
        }
        if (!rateUserService.getRateUsers(film.getId()).isEmpty()) {
            film.setRateUsers(rateUserService.getRateUsers(film.getId()).size());
        }
        if (!filmDirectorService.getFilmDirector(film.getId()).isEmpty()) {
            film.setDirectors(filmDirectorService.getFilmDirector(film.getId()));
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
        if (film.getDirectors() != null) {
            filmDirectorService.addFilmDirector(film.getId(), film.getDirectors());
            film.setDirectors(filmDirectorService.getFilmDirector(film.getId()));
        }
        return film;
    }

    public Film updateFilm(Film film) {
        if (film.getId() <= 0) {
            throw new NotFoundException("ID меньше или равно 0");
        }
        validate(film);
        filmStorage.updateFilm(film);
        filmGenreService.removeFilmGenre(film.getId());
        if (film.getGenres() != null) {
            filmGenreService.addFilmGenre(film.getId(), film.getGenres());
            film.setGenres(filmGenreService.getFilmGenres(film.getId()));
        }
        if (film.getDirectors() != null) {
            filmDirectorService.addFilmDirector(film.getId(), film.getDirectors());
            film.setDirectors(filmDirectorService.getFilmDirector(film.getId()));
        } else {
            filmDirectorService.removeFilmDirector(film.getId());
        }
        if (film.getRateUsers() != 0) {
            rateUserService.addRateUser(film.getId(), film.getRateUsers());
            film.setRateUsers(film.getRateUsers());
        } else {
            film.setRateUsers(0);
        }
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

    public List<Film> getFilmsByDirector(int directorId, Collection<String> sort) {
        List<Film> allFilms = getFilms();

        return allFilms;
    }

}
