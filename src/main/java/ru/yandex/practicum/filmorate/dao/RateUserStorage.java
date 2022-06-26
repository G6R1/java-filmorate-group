package ru.yandex.practicum.filmorate.dao;

import java.util.Set;

public interface RateUserStorage {

    Set<Long> getRateUsers(long filmId);

    void addRateUsers(long filmId, Set<Long> userRate);

    void removeRateUsers(long filmId);
}