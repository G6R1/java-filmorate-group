package ru.yandex.practicum.filmorate.model;


import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class Review {
    private long id;
    @NotNull
    private Long userId;
    @NotNull
    private Long filmId;
    @NotBlank
    //содержание отзыва
    private String content;
    //рейтинг полезности
    private long useful;
    @NotNull
    //тип отзыва
    private Boolean isPositive;

    public Review(long id, long userId, long filmId, String content, Boolean isPositive) {
        this.id = id;
        this.userId = userId;
        this.filmId = filmId;
        this.content = content;
        this.isPositive = isPositive;
    }
}
