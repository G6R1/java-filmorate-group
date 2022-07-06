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

    public List<Film>  getCommon(int userId,int friendId){
        return filmStorage.getCommon(userId, friendId);
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
        filmDirectorService.removeFilmDirectors(film.getId());
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

        Long rateId = eventService.getRateEntityId(userId, filmId);
        eventService.createEvent(userId, "LIKE", "ADD", rateId);

        return getFilm(filmId);
    }

    public Film removeLike(long filmId, long userId) {
        User user = userService.getUser(userId);
        Film film = getFilm(filmId);
        if (!rateUserService.getRateUsers(filmId).contains(userId))
            throw new NotFoundException("пользователь не ставил лайков");

        Long rateId = eventService.getRateEntityId(userId, filmId);
        eventService.createEvent(userId, "LIKE", "REMOTE", rateId);

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
        Collection<Film> filmSearchByDirector = filmStorage.getFilmsByDirector(directorId, sortBy);
        filmSearchByDirector.forEach(film -> {
            if (!filmGenreService.getFilmGenres(film.getId()).isEmpty())
                film.setGenres(filmGenreService.getFilmGenres(film.getId()));
        });
        return filmSearchByDirector;
    }

    public Collection<Film> getFilmSearch(String query, String sortBy) {
        Collection<Film> filmSearch = filmStorage.getFilmsSearch(query, sortBy);
        filmSearch.forEach(film -> {
            if (!filmGenreService.getFilmGenres(film.getId()).isEmpty())
                film.setGenres(filmGenreService.getFilmGenres(film.getId()));
        });
        return filmSearch;
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
        if (film.getDirectors() != null) {
            filmDirectorService.addFilmDirectors(film.getId(), film.getDirectors());
            film.setDirectors(filmDirectorService.getFilmDirectors(film.getId()));
        }
        if (film.getRateUsers() != 0) {
            rateUserService.addRateUser(film.getId(), film.getRateUsers());
            film.setRateUsers(film.getRateUsers());
        }
    }
}
