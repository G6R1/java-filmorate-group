package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private String name;
    @NotBlank(message = "login blank")
    @Pattern(regexp = "\\S*$")
    private String login;
    private long id;
    @NotBlank(message = "email blank")
    @Email(message = "invalid email")
    private String email;
    @NotBlank
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "invalid format birthday")
    private String birthday;
    private Set<Long> friends = new HashSet<>();

    public void addFriend(long friendId) {

        friends.add(friendId);
    }

    public void removeFriend(long friendId) {
        friends.remove(friendId);
    }
}
