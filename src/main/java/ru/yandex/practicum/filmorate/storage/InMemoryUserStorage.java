package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private long userId = 0;
    private Map<Long, User> users = new HashMap<>();

    @Override

    public void createUser(User user) {
        userId++;
        user.setId(userId);
        users.put(userId, user);
    }

    @Override
    public Optional<User> getUser(long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public void updateUser(User user) {
        users.replace(user.getId(), user);
    }

    @Override
    public void removeUser(long userId) {
        users.remove(userId);
    }

    public Map<Long, User> getUsers() {
        return users;
    }
}