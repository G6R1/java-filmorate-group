package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class FilmDirector {
    private long filmId;
    private int directorId;

    public FilmDirector(long filmId, int directorId) {
        this.filmId = filmId;
        this.directorId = directorId;
    }
}