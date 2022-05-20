package ru.yandex.practicum.filmorate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private int filmID = 0;
    private final Map<String, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getFilms() {
        log.debug("Текущее количество фильмов: {}", films.size());
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        if (validate(film)) {
            filmID++;
            film.setId(filmID);
            films.put(film.getName(), film);
        } else {
            throw new ValidationException("неправильный фильм");
        }
        log.debug("Добавлен фильм: {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (validate(film) && films.containsKey(film.getName()))
            films.put(film.getName(), film);
        else {
            throw new ValidationException("такого фильма нет в списке");
        }
        log.debug("Обновлён фильм: {}", film);
        return film;
    }

    private boolean validate(Film film) {
        return !LocalDate.parse(film.getReleaseDate()).isBefore(LocalDate.of(1895, 12, 28));
    }
}