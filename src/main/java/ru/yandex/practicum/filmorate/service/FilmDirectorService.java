package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDirectorStorage;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Set;

@Service
public class FilmDirectorService {
    private FilmDirectorStorage filmDirectorStorage;

    @Autowired
    public FilmDirectorService(FilmDirectorStorage filmDirectorStorage) {
        this.filmDirectorStorage = filmDirectorStorage;
    }

    void addFilmDirectors(long filmId, Set<Director> directors) {
        directors.forEach((director) -> filmDirectorStorage.addFilmDirector(filmId, director.getId()));
    }

    Set<Director> getFilmDirectors(long filmId) {
        return filmDirectorStorage.getFilmDirectors(filmId);
    }

    void removeFilmDirectors(long filmId) {
        filmDirectorStorage.removeFilmDirector(filmId);
    }
}