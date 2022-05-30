package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private int filmId = 0;
    private final Map<Long, Film> films = new HashMap<>();

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