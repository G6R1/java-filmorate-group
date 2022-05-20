package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

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
}