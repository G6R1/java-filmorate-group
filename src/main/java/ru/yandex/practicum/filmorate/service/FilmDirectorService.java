package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDirectorStorage;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmDirector;

import java.util.Collection;
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

    public Set<Director> getFilmDirector(long filmId) {
        return filmDirectorStorage.getFilmDirector(filmId)
                .stream()
                .map(FilmDirector::getDirectorId)
                .map(directorService::getDirector)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    void addFilmDirector(long filmId, Set<Director> directors) {
        directors.forEach((director) -> filmDirectorStorage.addFilmDirector(filmId, director.getId()));
    }

    void removeFilmDirector(long filmId) {
        filmDirectorStorage.removeFilmDirector(filmId);
    }

    public Collection<Film> getFilmsByDirector(int directorId, Collection<String> sort) {
        return filmDirectorStorage.getFilmsByDirector(directorId, sort);
    }
}
