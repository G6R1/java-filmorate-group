package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

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
    int rate = 0;
    Set<Integer> likes = new HashSet<>();

    public void addLike(int userId) {
        rate++;
        likes.add(userId);
    }

    public void removeLike(int userId) {
        rate--;
        likes.remove(userId);
    }

    public static final Comparator<Film>
            COMPARE_BY_RATE = (film1, film2) -> film2.getRate() - film1.getRate();
}