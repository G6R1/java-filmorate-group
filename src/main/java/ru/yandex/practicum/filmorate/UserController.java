package ru.yandex.practicum.filmorate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private int userID = 0;
    private final Map<String, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getUsers() {
        log.debug("Текущее количество пользователей: {}", users.size());
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        if (validate(user)) {
            userID++;
            user.setId(userID);
            users.put(user.getLogin(), user);
        } else {
            throw new ValidationException("неправильный пользователь");
        }
        log.debug("Добавлен пользователь: {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (validate(user) && users.containsKey(user.getLogin()))
            users.put(user.getLogin(), user);
        else {
            throw new ValidationException("такого пользователя нет в списке");
        }
        log.debug("Обновлён пользователь: {}", user);
        return user;
    }

    private boolean validate(User user) {
        if (LocalDate.parse(user.getBirthday()).isAfter(LocalDate.now())) return false;
        if (user.getLogin().isEmpty() && user.getLogin().contains(" ")) return false;
        if (user.getName() == null || user.getName().isEmpty()) user.setName(user.getEmail());
        return true;
    }
}