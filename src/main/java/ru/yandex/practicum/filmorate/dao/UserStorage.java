package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;
import java.util.Optional;

public interface UserStorage {

    //создаёт пользователя
    void createUser(User user);

    //возвращает пользователя по идентификатору
    Optional<User> findUserById(long userId);

    //обновляет пользователя
    void updateUser(User user);

    //удаляет пользователя по идентификатору
    void removeUser(long userId);

    //возвращает список всех пользователей
    Map<Long, User> getUsers();
}
