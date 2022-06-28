package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class FilmGenre {
    private long filmId;
    private int genreId;

    public FilmGenre(long filmId, int genreId) {
        this.filmId = filmId;
        this.genreId = genreId;
    }
}
