package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.DirectorStorage;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Component
@RestController
public class DirectorController {
    private final DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @PostMapping("/directors")
    public Director create(@Valid @RequestBody Director director) throws ValidationException {
        log.info("Получен запрос на создание нового режиссера");
        return directorService.createDirector(director);
    }

    @PutMapping("/directors")
    public Director updateDirector(@RequestBody Director director) {
        log.info("Получен запрос на обновление данных режиссера");
        return directorService.updateDirector(director);
    }

    @GetMapping("/directors/{id}")
    public Director getDirectorById(@PathVariable int id) {
        log.info("Получен запрос на получение данных режиссера");
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
        directorService.removeDirector(id);
    }
}
