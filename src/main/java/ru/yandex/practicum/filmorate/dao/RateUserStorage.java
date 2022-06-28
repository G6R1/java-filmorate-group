package ru.yandex.practicum.filmorate.dao;

import java.util.Set;

public interface RateUserStorage {

    Set<Long> getRateUsers(long filmId);

    void addRateUser(long filmId, long userId);

    void removeRateUser(long filmId, long userId);
}