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
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private FilmDirectorService filmDirectorService;
    private FilmGenreService filmGenreService;
    private FilmStorage filmStorage;
    private RateUserService rateUserService;
    private UserService userService;

    @Autowired
    public FilmService(
            FilmDirectorService filmDirectorService
            , FilmGenreService filmGenreService
            , FilmStorage filmStorage
            , RateUserService rateUserService
            , UserService userService) {
        this.filmDirectorService = filmDirectorService;
        this.filmGenreService = filmGenreService;
        this.filmStorage = filmStorage;
        this.rateUserService = rateUserService;
        this.userService = userService;
    }

    public Film getFilm(long filmId) {
        Film film = filmStorage.getFilm(filmId).orElseThrow(() -> new NotFoundException("такого фильма нет в списке"));
        if (!filmGenreService.getFilmGenres(filmId).isEmpty()) {
            film.setGenres(filmGenreService.getFilmGenres(filmId));
        }
        if (!rateUserService.getRateUsers(filmId).isEmpty()) {
            film.setRateUsers(rateUserService.getRateUsers(filmId).size());
        }
        if (!filmDirectorService.getFilmDirectors(filmId).isEmpty())
            film.setDirectors(filmDirectorService.getFilmDirectors(filmId));
        return film;
    }

    public Film createFilm(Film film) {
        filmStorage.createFilm(film);
        validate(film);
        return film;
    }

    public Film updateFilm(Film film) {
        getFilm(film.getId());
        filmGenreService.removeFilmGenre(film.getId());
        filmDirectorService.removeFilmDirectors(film.getId());
        validate(film);
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

    public Collection<Film> getFilmsByDirector(int directorId, Collection<String> sortBy) {
        filmDirectorService.getDirector(directorId);
        return filmStorage.getFilmsByDirector(directorId, sortBy);
    }

    public Collection<Film> getFilmSearch(String query, String sortBy) {
        return filmStorage.getFilmsSearch(query, sortBy);
    }

    private void validate(Film film) {
        if (LocalDate.parse(film.getReleaseDate()).isBefore(LocalDate.of(1895, 12, 28))) {
            filmStorage.removeFilm(film.getId());
            throw new ValidationException("неправильный фильм");
        }
        if (film.getGenres() != null) {
            filmGenreService.addFilmGenre(film.getId(), film.getGenres());
            film.setGenres(filmGenreService.getFilmGenres(film.getId()));
        }
        if(film.getDirectors() != null) {
            filmDirectorService.addFilmDirectors(film.getId(), film.getDirectors());
            film.setDirectors(filmDirectorService.getFilmDirectors(film.getId()));
        }
        if (film.getRateUsers() != 0) {
            rateUserService.addRateUser(film.getId(), film.getRateUsers());
            film.setRateUsers(film.getRateUsers());
        }
    }
}
