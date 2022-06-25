package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RateMpa;

import java.util.*;

@Component
@Qualifier("InMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {
    private int filmId = 0;
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public List<RateMpa> getAllMpa() {
        return new ArrayList<>();
    }

    @Override
    public Optional<RateMpa> getRateMpa(int mpaId) {
        return Optional.empty();
    }

    @Override
    public List<Genre> getAllGenres() {
        return new ArrayList<>();
    }

    @Override
    public Optional<Genre> getGenre(int genreId) {
        return Optional.empty();
    }

    @Override
    public void createFilm(Film film) {
        filmId++;
        film.setId(filmId);
        films.put(film.getId(), film);
    }

    @Override
    public Optional<Film> getFilm(long filmId) {
        return Optional.ofNullable(films.get(filmId));
    }

    @Override
    public void updateFilm(Film film) {
        films.put(film.getId(), film);
    }

    @Override
    public void removeFilm(long filmId) {
        films.remove(filmId);
    }

    @Override
    public Map<Long, Film> getFilms() {
        return films;
    }
}
