package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorStorage {

    void createDirector(Director director);

    void updateDirector(Director director);

    void removeDirector(int id);

    Optional<Director> getDirector(int id);

    List<Director> getListDirectors();
}
