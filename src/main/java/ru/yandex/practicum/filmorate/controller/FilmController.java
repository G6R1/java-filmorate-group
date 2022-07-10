package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.SortType;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

@RestController
@Slf4j
@Validated
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/films/common")
    public Collection<Film> getCommonFilms(@RequestParam Long userId,
                                     @RequestParam Long friendId) {
        return filmService.getCommon(userId, friendId);
    }

    @GetMapping("/films")
    public Collection<Film> getFilms() {
        log.debug("Текущее количество фильмов: {}", filmService.getFilms().size());
        return filmService.getFilms();
    }

    @GetMapping("/films/{id}")
    public Film film(@PathVariable Long id) {
        return filmService.getFilm(id);
    }

    @PostMapping("/films")
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@Valid @RequestBody Film film) {
        log.debug("Добавлен фильм: {}", film);
        return filmService.createFilm(film);
    }

    @PutMapping("/films")
    public Film update(@Valid @RequestBody Film film) {
        log.debug("Обновлён фильм: {}", film);
        return filmService.updateFilm(film);
    }

    @DeleteMapping("/films/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void removeFilm(@PathVariable Long id) {
        filmService.removeFilm(id);
    }

    @PutMapping("/films/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public Film add(@PathVariable Long id, @PathVariable Long userId) {
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public Film delete(@PathVariable Long id, @PathVariable Long userId) {
        return filmService.removeLike(id, userId);
    }

    @GetMapping("/films/director/{directorId}")
    public Collection<Film> getFilmsByDirector(@PathVariable int directorId,
                                               @RequestParam SortType sortBy) {
        return filmService.getFilmsByDirector(directorId, sortBy);
    }

    @GetMapping("films/search")
    public Collection<Film> getFilmsSearch(@RequestParam String query,
                                           @RequestParam EnumSet<SortType> by) {
        return filmService.getFilmSearch(query, by);
    }

    @GetMapping("/films/popular")
    public Collection<Film> films(@Positive @RequestParam(defaultValue = "10", required = false) Integer count,
                                  @Positive @RequestParam(required = false) Integer genreId,
                                  @Positive @RequestParam(required = false) String year) {
        return filmService.getFilmsPopular(count, genreId, year);
    }
}
