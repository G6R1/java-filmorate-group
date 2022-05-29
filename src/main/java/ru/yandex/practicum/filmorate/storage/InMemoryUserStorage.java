package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private int userId = 0;
    private Map<Integer, User> users = new HashMap<>();

    @Override

    public User createUser(User user) {
        if (validate(user) || users.containsValue(user)) {
            throw new ValidationException("неправильный пользователь");
        } else {
            userId++;
            user.setId(userId);
            users.put(userId, user);
        }
        return user;
    }

    @Override
    public User getUser(int userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("такого пользователя нет в списке");
        } else {
            return users.get(userId);
        }
    }

    @Override
    public User updateUser(User user) {
        if (validate(user) || !users.containsKey(user.getId())) {
            throw new NotFoundException("такого пользователя нет в списке");
        } else {
            users.replace(user.getId(), user);
        }
        return users.get(user.getId());
    }

    @Override
    public void removeUser(int userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("такого пользователя нет в списке");
        } else {
            users.remove(userId);
        }
    }

    @Override
    public List<User> getUsers() {
        List<User> userList = new ArrayList<>(0);
        for (Integer key : users.keySet()) {
            User user = users.get(key);
            userList.add(user);
        }
        return userList;
    }

    private boolean validate(User user) {
        if (LocalDate.parse(user.getBirthday()).isAfter(LocalDate.now())) return true;
        if (user.getLogin().isEmpty() && user.getLogin().contains(" ")) return true;
        if (user.getName() == null || user.getName().isBlank()) user.setName(user.getLogin());
        return false;
    }
}