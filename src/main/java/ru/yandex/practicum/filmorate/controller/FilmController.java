package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmDirectorService;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.Collection;
import java.util.List;

@RestController
@Slf4j
@Validated
public class FilmController {
    private final FilmService filmService;
    private final FilmDirectorService filmDirectorService;

    @Autowired
    public FilmController(FilmService filmService, FilmDirectorService filmDirectorService) {
        this.filmService = filmService;
        this.filmDirectorService = filmDirectorService;
    }

    @GetMapping("/films")
    public Collection<Film> getFilms() {
        log.debug("Текущее количество фильмов: {}", filmService.getFilms().size());
        return filmService.getFilms();
    }

    @GetMapping("/films/{id}")
    public Film getFilm(@PathVariable Long id) {
        return filmService.getFilm(id);
    }

    @PostMapping("/films")
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@Valid @RequestBody Film film) {
        log.debug("Добавлен фильм: {}", film);
        return filmService.createFilm(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.debug("Обновлён фильм: {}", film);
        return filmService.updateFilm(film);
    }

    @DeleteMapping("/films/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void removeFilm(@PathVariable Long id) {
        filmService.removeFilm(id);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public Film addLike(@PathVariable Long id, @PathVariable Long userId) {
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public Film removeLike(@PathVariable Long id, @PathVariable Long userId) {
        return filmService.removeLike(id, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> getPopular(@Positive @RequestParam(defaultValue = "10", required = false) int count) {
        return filmService.getPopular(count);
    }

    @GetMapping("/films/director/{directorId}")
    public Collection<Film> getFilmsByDirector(@PathVariable int directorId,
                                               @RequestParam Collection<String> sortBy) {
        return filmService.getFilmsByDirector(directorId, sortBy);
    }
}