package ru.yandex.practicum.filmorate.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class Review {
    private long reviewId;
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

    private Boolean deleted;

    public Review(long reviewId, long userId, long filmId, String content, Boolean isPositive) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.filmId = filmId;
        this.content = content;
        this.isPositive = isPositive;
    }

    @JsonCreator
    public Review(long reviewId, Long userId, Long filmId, String content, long useful, Boolean isPositive) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.filmId = filmId;
        this.content = content;
        this.useful = useful;
        this.isPositive = isPositive;
    }

}
