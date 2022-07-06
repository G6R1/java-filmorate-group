package ru.yandex.practicum.filmorate.model;


import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Comparator;

@Data
public class Review {
    private long id;
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
    private int useful;

    public static final Comparator<Review>
            COMPARE_BY_USEFUL = (review1, review2) -> review2.getUseful() - review1.getUseful();
}