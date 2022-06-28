package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.RateUserStorage;

import java.util.Set;

@Service
public class RateUserService {
    private RateUserStorage rateUserStorage;

    @Autowired
    public RateUserService(RateUserStorage rateUserStorage) {
        this.rateUserStorage = rateUserStorage;
    }

    Set<Long> getRateUsers(long filmId) {
        return rateUserStorage.getRateUsers(filmId);
    }

    void addRateUser(long filmId, long userId) {
        rateUserStorage.addRateUser(filmId, userId);
    }

    void removeRateUser(long filmId, long userId) {
        rateUserStorage.removeRateUser(filmId, userId);
    }
}
