package ru.yandex.practicum.filmorate.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class Review {
    private long reviewId;
    @NotBlank
    //содержание отзыва
    private String content;
    @NotNull
    //тип отзыва
    private Boolean isPositive;
    @NotNull
    private Long userId;
    @NotNull
    private Long filmId;
    //рейтинг полезности
    private long useful;
    private Boolean deleted;

    public Review(long reviewId, String content, Boolean isPositive, long userId, long filmId) {
        this.reviewId = reviewId;
        this.content = content;
        this.isPositive = isPositive;
        this.userId = userId;
        this.filmId = filmId;
    }

    @JsonCreator
    public Review(long reviewId, String content, Boolean isPositive, Long userId, Long filmId, long useful) {
        this.reviewId = reviewId;
        this.content = content;
        this.isPositive = isPositive;
        this.userId = userId;
        this.filmId = filmId;
        this.useful = useful;
    }
}
