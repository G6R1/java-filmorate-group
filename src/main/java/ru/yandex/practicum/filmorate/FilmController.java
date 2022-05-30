package ru.yandex.practicum.filmorate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.Collection;
import java.util.List;

@RestController
@Slf4j
@Validated
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> getFilms() {
        log.debug("Текущее количество фильмов: {}", filmService.getFilms().size());
        return filmService.getFilms();
    }

    @GetMapping(value = "/{id}")
    public Film getFilm(@PathVariable Long id) {
        return filmService.getFilm(id);
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.debug("Добавлен фильм: {}", film);
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.debug("Обновлён фильм: {}", film);
        return filmService.updateFilm(film);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFilm(@PathVariable Long id) {
        filmService.removeFilm(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable Long id, @PathVariable Long userId) {
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film removeLike(@PathVariable Long id, @PathVariable Long userId) {
        return filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@Positive @RequestParam(defaultValue = "10", required = false) int count) {
        return filmService.getPopular(count);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
        return new ResponseEntity<>("неверное значение параметра: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
