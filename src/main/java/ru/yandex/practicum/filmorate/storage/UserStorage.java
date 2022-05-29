package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    //создаёт пользователя
    User createUser(User user);

    //возвращает пользователя по идентификатору
    User getUser(int userId);

    //обновляет пользователя
    User updateUser(User user);

    //удаляет пользователя по идентификатору
    void removeUser(int userId);

    //возвращает список пользователей
    Collection<User> getUsers();
}