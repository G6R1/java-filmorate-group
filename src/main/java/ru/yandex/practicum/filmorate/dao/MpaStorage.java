package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.RateMpa;

import java.util.List;
import java.util.Optional;

public interface MpaStorage {

    List<RateMpa> getAllMpa();

    Optional<RateMpa> getRateMpa(int mpaId);
}