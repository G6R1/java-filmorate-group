package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDirectorStorage;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.FilmDirector;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmDirectorService {
    private FilmDirectorStorage filmDirectorStorage;
    private DirectorService directorService;

    @Autowired
    public FilmDirectorService(FilmDirectorStorage filmDirectorStorage, DirectorService directorService) {
        this.filmDirectorStorage = filmDirectorStorage;
        this.directorService = directorService;
    }

    public Set<Director> getFilmDirectors(long filmId) {
        return filmDirectorStorage.getFilmDirectors(filmId)
                .stream()
                .map(FilmDirector::getDirectorId)
                .map(directorService::getDirector)
                .sorted(Comparator.comparing(Director::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    void addFilmDirectors(long filmId, Set<Director> directors) {
        directors.forEach((director) -> filmDirectorStorage.addFilmDirector(filmId, director.getId()));
    }

    void removeFilmDirectors(long filmId) {
        filmDirectorStorage.removeFilmDirector(filmId);
    }

    void getDirector(int directorId) {
        directorService.getDirector(directorId);
    }
}