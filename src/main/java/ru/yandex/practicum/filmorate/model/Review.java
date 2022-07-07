package ru.yandex.practicum.filmorate.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
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
    private Long useful;
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
