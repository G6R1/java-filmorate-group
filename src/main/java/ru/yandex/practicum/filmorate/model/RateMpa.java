package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Positive;

@Data
public class RateMpa {
    @Positive
    private int id;
    private String name;
    private String mpaDescription;
}
