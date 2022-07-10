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
import java.util.Collection;
import java.util.EnumSet;

@Service
public class FilmService {
    final private DirectorService directorService;
    final private FilmGenreService filmGenreService;
    final private FilmStorage filmStorage;
    final private MpaService mpaService;
    final private RateUserService rateUserService;
    final private UserService userService;
    final private EventService eventService;
    final private FilmDirectorService filmDirectorService;

    @Autowired
    public FilmService(DirectorService directorService,
                       FilmGenreService filmGenreService,
                       FilmStorage filmStorage,
                       MpaService mpaService,
                       RateUserService rateUserService,
                       UserService userService,
                       EventService eventService,
                       FilmDirectorService filmDirectorService) {
        this.directorService = directorService;
        this.filmGenreService = filmGenreService;
        this.filmStorage = filmStorage;
        this.mpaService = mpaService;
        this.rateUserService = rateUserService;
        this.userService = userService;
        this.eventService = eventService;
        this.filmDirectorService = filmDirectorService;
    }

    public Collection<Film> getCommon(long userId, long friendId) {
        Collection<Film> common = filmStorage.getCommon(userId, friendId);
        common.forEach(this::filmVariablesSet);
        return common;
    }

    public Film getFilm(long filmId) {
        Film film = filmStorage.getFilm(filmId).orElseThrow(()
                -> new NotFoundException("такого фильма нет в списке"));
        filmVariablesSet(film);
        return film;
    }

    public Film createFilm(Film film) {
        validate(film);
        filmStorage.createFilm(film);
        filmVariablesGet(film);
        return film;
    }

    public Film updateFilm(Film film) {
        validate(film);
        getFilm(film.getId());
        filmGenreService.removeFilmGenre(film.getId());
        filmDirectorService.removeFilmDirectors(film.getId());
        filmStorage.updateFilm(film);
        filmVariablesGet(film);
        return film;
    }

    public void removeFilm(long filmId) {
        getFilm(filmId);
        filmStorage.removeFilm(filmId);
    }

    public Collection<Film> getFilms() {
        Collection<Film> films = filmStorage.getFilms();
        films.forEach(this::filmVariablesSet);
        return films;
    }

    public Film addLike(long filmId, long userId) {
        Film film = getFilm(filmId);
        User user = userService.getUser(userId);
        rateUserService.addRateUser(film.getId(), user.getId());
        updateFilm(film);
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
        updateFilm(film);
        return getFilm(filmId);
    }

    public Collection<Film> getFilmsByDirector(int directorId, SortType sortBy) {
        directorService.getDirector(directorId);
        Collection<Film> filmSearchByDirector = filmStorage.getFilmsByDirector(directorId, sortBy);
        filmSearchByDirector.forEach(this::filmVariablesSet);
        return filmSearchByDirector;
    }

    public Collection<Film> getFilmSearch(String query, EnumSet<SortType> sortBy) {
        Collection<Film> filmSearch = filmStorage.getFilmsSearch(query, sortBy);
        filmSearch.forEach(this::filmVariablesSet);
        return filmSearch;
    }

    public Collection<Film> getFilmsPopular(int count, Integer genre, String year) {
        Collection<Film> filmSearch = filmStorage.getFilmsPopular(count, genre, year);
        filmSearch.forEach(this::filmVariablesSet);
        return filmSearch;
    }

    private void validate(Film film) {
        if (LocalDate.parse(film.getReleaseDate()).isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Некорректная дата релиза фильма");
        }
    }

    private void filmVariablesSet(Film film) {
        film.setMpa(mpaService.getFilmMpa(film.getId()));
        if (!filmGenreService.getFilmGenres(film.getId()).isEmpty()) {
            film.setGenres(filmGenreService.getFilmGenres(film.getId()));
        }
        if (!rateUserService.getRateUsers(film.getId()).isEmpty()) {
            film.setRateUsers(rateUserService.getRateUsers(film.getId()).size());
        }
        film.setDirectors(filmDirectorService.getFilmDirectors(film.getId()));
    }

    private void filmVariablesGet(Film film) {
        film.setMpa(mpaService.getFilmMpa(film.getId()));
        if (film.getGenres() != null) {
            filmGenreService.addFilmGenre(film.getId(), film.getGenres());
            film.setGenres(filmGenreService.getFilmGenres(film.getId()));
        }
        if (film.getDirectors() != null) {
            filmDirectorService.addFilmDirectors(film.getId(), film.getDirectors());
            film.setDirectors(filmDirectorService.getFilmDirectors(film.getId()));
        }
    }
}