package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorStorage;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.List;

@Service
public class DirectorService {
    private DirectorStorage directorStorage;

    @Autowired
    public DirectorService(DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    public Director getDirector(int id) {
        return directorStorage.getDirector(id)
                .orElseThrow(() -> new NotFoundException("такого режиссера не существует"));
    }

    public List<Director> getAllDirectors() {
        return directorStorage.getListDirectors();
    }
}
