package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

@Data
public class User {
    @NotBlank(message = "login blank")
    @Pattern(regexp = "\\S*$")
    private String login;
    private String name;
    private int id;
    @NotBlank(message = "email blank")
    @Email(message = "invalid email")
    private String email;
    @NotBlank
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "invalid format birthday")
    private String birthday;
    private Set<Integer> friends = new HashSet<>();

    public void addFriend(int friendId) {
        friends.add(friendId);
    }

    public void removeFriend(int friendId) {
        friends.remove(friendId);
    }
}