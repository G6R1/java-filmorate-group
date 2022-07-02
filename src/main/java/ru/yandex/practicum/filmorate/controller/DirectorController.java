package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.DirectorStorage;
import ru.yandex.practicum.filmorate.dao.FilmDirectorStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.service.FilmDirectorService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RestController
public class DirectorController {

    private final DirectorStorage directorStorage;
    private final DirectorService directorService;

    private final FilmDirectorService filmDirectorService;

    @Autowired
    public DirectorController(DirectorStorage directorStorage, DirectorService directorService,
                              FilmDirectorService filmDirectorService) {
        this.directorStorage = directorStorage;
        this.directorService = directorService;
        this.filmDirectorService = filmDirectorService;
    }

    @PostMapping("/directors")
    public Director create(@Valid @RequestBody Director director) throws ValidationException {
        log.info("Получен запрос на создание нового режиссера");
        return directorStorage.createDirector(director);
    }

    @PutMapping("/directors")
    public Director updateDirector(@RequestBody Director director) {
        log.info("Получен запрос на обновление данных режиссера");
        if (director.getId() <= 0 & director.getName() == null) {
            throw new NotFoundException("ID режиссера меньше или равно 0");
        }
        return directorStorage.updateDirector(director);
    }

    @GetMapping("/directors/{id}")
    public Director getDirectorById(@PathVariable int id) {
        log.info("Получен запрос на получение данных режиссера");
        if (id <= 0) {
            throw new NotFoundException("ID режиссера меньше или равно 0");
        }
        return directorService.getDirector(id);
    }

    @GetMapping("/directors")
    public List<Director> getListDirectors() {
        log.info("Получен запрос списка режиссеров");
        return directorService.getAllDirectors();
    }

    @DeleteMapping("/directors/{id}")
    public void removeDirectorById(@PathVariable int id) {
        log.info("Получен запрос на удаление данных режиссера");
        if (id <= 0) {
            throw new NotFoundException("ID режиссера меньше или равно 0");
        }
       directorStorage.removeDirector(id);
    }

    @GetMapping("/films/director/{directorId}")
    public Collection<Film> getFilmsByDirector(@PathVariable int directorId,
                                               @RequestParam Collection<String> sortBy) {
        return filmDirectorService.getFilmsByDirector(directorId, sortBy);
    }
}
