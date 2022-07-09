package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@Validated
public class DirectorController {
    private final DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @PostMapping("/directors")
    @ResponseStatus(HttpStatus.CREATED)
    public Director create(@Valid @RequestBody Director director) {
        log.info("Получен запрос на создание нового режиссера");
        return directorService.createDirector(director);
    }

    @PutMapping("/directors")
    public Director update(@Valid @RequestBody Director director) {
        log.info("Получен запрос на обновление данных режиссера");
        return directorService.updateDirector(director);
    }

    @DeleteMapping("/directors/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@Valid @PathVariable int id) {
        directorService.removeDirector(id);
    }

    @GetMapping("/directors/{id}")
    public Director getById(@Valid @PathVariable int id) {
        log.info("Получен запрос на получение данных режиссера");
        return directorService.getDirector(id);
    }

    @GetMapping("/directors")
    public List<Director> getAll() {
        log.info("Получен запрос списка режиссеров");
        return directorService.getAllDirectors();
    }
}