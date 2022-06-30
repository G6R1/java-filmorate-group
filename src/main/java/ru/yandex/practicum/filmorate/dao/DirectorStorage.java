package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface DirectorStorage {

    Director createDirector(Director director);

    Director updateDirector(Director director);

    Optional<Director> removeDirector(int id);

    Optional<Director> getDirector(int directorId);

    List<Director> getListDirectors();
}

