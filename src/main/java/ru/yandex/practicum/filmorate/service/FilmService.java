package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.SortType;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

@Service
public class FilmService {
    final private FilmGenreService filmGenreService;
    final private FilmStorage filmStorage;
    final private RateUserService rateUserService;
    final private UserService userService;
    final private EventService eventService;
    final private FilmDirectorService filmDirectorService;

    @Autowired
    public FilmService(FilmGenreService filmGenreService,
                       FilmStorage filmStorage,
                       RateUserService rateUserService,
                       UserService userService,
                       EventService eventService,
                       FilmDirectorService filmDirectorService) {
        this.filmGenreService = filmGenreService;
        this.filmStorage = filmStorage;
        this.rateUserService = rateUserService;
        this.userService = userService;
        this.eventService = eventService;
        this.filmDirectorService = filmDirectorService;
    }

    public List<Film> getCommon(long userId, long friendId) {
        List<Film> common = filmStorage.getCommon(userId, friendId);
        common.forEach(film -> {
            if (!filmGenreService.getFilmGenres(film.getId()).isEmpty())
                film.setGenres(filmGenreService.getFilmGenres(film.getId()));
        });
        return common;
    }

    public Film getFilm(long filmId) {
        Film film = filmStorage.getFilm(filmId).orElseThrow(()
                -> new NotFoundException("такого фильма нет в списке"));
        if (!filmGenreService.getFilmGenres(filmId).isEmpty()) {
            film.setGenres(filmGenreService.getFilmGenres(filmId));
        }
        if (!rateUserService.getRateUsers(filmId).isEmpty()) {
            film.setRateUsers(rateUserService.getRateUsers(filmId).size());
        }
        film.setDirectors(filmDirectorService.getDirectorFromFilm(filmId));
        return film;
    }

    public Film createFilm(Film film) {
        validate(film);
        filmStorage.createFilm(film);
        filmVariablesCheck(film);
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
        filmDirectorService.removeFilmDirectors(film.getId());
        filmVariablesCheck(film);
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
        List<Film> allFilms = new ArrayList<>(filmStorage.getFilms().values());
        for (Film film : allFilms) {
            film.setDirectors(filmDirectorService.getDirectorFromFilm(film.getId()));
        }
        return allFilms;
    }

    public Film addLike(long filmId, long userId) {
        Film film = getFilm(filmId);
        User user = userService.getUser(userId);
        rateUserService.addRateUser(film.getId(), user.getId());
        filmStorage.updateFilm(film);

        eventService.createEvent(userId, EventType.LIKE, EventOperation.ADD, filmId);

        return getFilm(filmId);
    }

    public Film removeLike(long filmId, long userId) {
        User user = userService.getUser(userId);
        Film film = getFilm(filmId);
        if (!rateUserService.getRateUsers(filmId).contains(userId))
            throw new NotFoundException("пользователь не ставил лайков");

        eventService.createEvent(userId, EventType.LIKE, EventOperation.REMOVE, filmId);

        rateUserService.removeRateUser(film.getId(), user.getId());
        filmStorage.updateFilm(film);
        return getFilm(filmId);
    }

    public Collection<Film> getFilmsByDirector(int directorId, SortType sortBy) {
        filmDirectorService.getDirector(directorId);
        Collection<Film> filmSearchByDirector = filmStorage.getFilmsByDirector(directorId, sortBy);
        filmSearchByDirector.forEach(film -> {
            if (!filmGenreService.getFilmGenres(film.getId()).isEmpty())
                film.setGenres(filmGenreService.getFilmGenres(film.getId()));
        });
        filmSearchByDirector.forEach(film -> {
            if (!filmDirectorService.getFilmDirectors(film.getId()).isEmpty())
                film.setDirectors(filmDirectorService.getDirectorFromFilm(film.getId()));
        });
        return filmSearchByDirector;
    }

    public Collection<Film> getFilmSearch(String query, EnumSet<SortType> sortBy) {
        Collection<Film> filmSearch = filmStorage.getFilmsSearch(query, sortBy);
        filmSearch.forEach(film -> {
            if (!filmGenreService.getFilmGenres(film.getId()).isEmpty())
                film.setGenres(filmGenreService.getFilmGenres(film.getId()));
        });
        filmSearch.forEach(film -> {
            if (!filmDirectorService.getFilmDirectors(film.getId()).isEmpty())
                film.setDirectors(filmDirectorService.getDirectorFromFilm(film.getId()));
        });
        return filmSearch;
    }

    private void validate(Film film) {
        if (LocalDate.parse(film.getReleaseDate()).isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Некорректная дата релиза фильма");
        }
    }

    private void filmVariablesCheck(Film film) {
        if (film.getDirectors() != null) {
            filmDirectorService.addFilmDirectors(film.getId(), film.getDirectors());
            film.setDirectors(filmDirectorService.getFilmDirectors(film.getId()));
        }
        if (film.getRateUsers() != 0) {
            rateUserService.addRateUser(film.getId(), film.getRateUsers());
            film.setRateUsers(film.getRateUsers());
        }
    }

    public Collection<Film> getFilmsPopular(int count, Integer genre, String year) {
        Collection<Film> filmSearch = filmStorage.getFilmsPopular(count, genre, year);
        filmSearch.forEach(film -> {
            if (!filmGenreService.getFilmGenres(film.getId()).isEmpty())
                film.setGenres(filmGenreService.getFilmGenres(film.getId()));
        });
        return filmSearch;
    }
}