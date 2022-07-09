package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class RateUsers {
    private long filmId;
    private long userId;

    public RateUsers(long filmId, long userId) {
        this.filmId = filmId;
        this.userId = userId;
    }
}
