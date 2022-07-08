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
    public Director create(@Valid @RequestBody Director director) {
        log.info("Получен запрос на создание нового режиссера");
        return directorService.createDirector(director);
    }

    @PutMapping("/directors")
    public Director update(@RequestBody Director director) {
        log.info("Получен запрос на обновление данных режиссера");
        if (director.getId() <= 0 & director.getName() == null) {
            throw new NotFoundException("ID режиссера меньше или равно 0");
        }
        return directorService.updateDirector(director);
    }

    @DeleteMapping("/directors/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable int id) {
        directorService.removeDirector(id);
    }

    @GetMapping("/directors/{id}")
    public Director getById(@PathVariable int id) {
        log.info("Получен запрос на получение данных режиссера");
        return directorService.getDirector(id);
    }

    @GetMapping("/directors")
    public List<Director> getAll() {
        log.info("Получен запрос списка режиссеров");
        return directorService.getAllDirectors();
    }
}
