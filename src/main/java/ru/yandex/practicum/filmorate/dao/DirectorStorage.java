package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorStorage {

    Director createDirector(Director director);

    Director updateDirector(Director director);

     void removeDirector(int id);

    Optional<Director> getDirector(int directorId);

    List<Director> getListDirectors();
}

