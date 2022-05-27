package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private int filmId = 0;
    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public Film createFilm(Film film) {
        if (validate(film) || films.containsValue(film)) {
            throw new ValidationException("неправильный фильм");
        } else {
            filmId++;
            film.setId(filmId);
            films.put(film.getId(), film);
        }
        return film;
    }

    @Override
    public Film getFilm(int filmId) {
        if (!films.containsKey(filmId)) {
            throw new NotFoundException("такого фильма нет в списке");
        } else {
            return films.get(filmId);
        }
    }

    @Override
    public Film updateFilm(Film film) {
        if (validate(film) || !films.containsKey(film.getId())) {
            throw new NotFoundException("такого фильма нет в списке");
        } else {
            films.put(film.getId(), film);
            return film;
        }
    }

    @Override
    public void removeFilm(int idFilm) {
        if (!films.containsKey(idFilm)) {
            throw new NotFoundException("такого фильма нет в списке");
        } else {
            films.remove(idFilm);
        }
    }

    @Override
    public List<Film> getFilms() {
        List<Film> filmList = new ArrayList<>(0);
        for (Integer key : films.keySet()) {
            Film film = films.get(key);
            filmList.add(film);
        }
        return filmList;
    }

    private boolean validate(Film film) {
        return LocalDate.parse(film.getReleaseDate()).isBefore(LocalDate.of(1895, 12, 28));
    }
}