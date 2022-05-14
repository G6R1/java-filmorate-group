package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class Film {
    @NotBlank(message = "incorrect name")
    String name;
    int id;
    @NotBlank(message = "incorrect description")
    @Size(max = 200, message = "max length 200")
    String description;
    @NotBlank(message = "incorrect releaseDate")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "invalid format releaseDate")
    String releaseDate;
    @NotNull
    @Positive
    int duration;
}