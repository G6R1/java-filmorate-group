package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

@Data

public class Film {
    @NotBlank(message = "incorrect name")
    private String name;
    private long id;
    @NotBlank(message = "incorrect description")
    @Size(max = 200, message = "max length 200")
    private String description;
    @NotBlank(message = "incorrect releaseDate")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "invalid format releaseDate")
    private String releaseDate;
    @NotNull
    @Positive
    private int duration;
    private RateMpa mpa = new RateMpa();
    private Set<Genre> genres;
    private Set<Long> rateUsers = new HashSet<>();


    public void addLike(long userId) {
        rateUsers.add(userId);
    }

    public void removeLike(long userId) {
        rateUsers.remove(userId);
    }

    public static final Comparator<Film>
            COMPARE_BY_RATE = (film1, film2) -> film2.getRateUsers().size() -
            film1.getRateUsers().size();
}