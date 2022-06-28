package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dao.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User getUser(long userId) {
        return userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("такого пользователя нет в списке"));
    }

    public User createUser(User user) {
        validate(user);
        userStorage.createUser(user);
        return user;
    }

    public User updateUser(User user) {
        validate(user);
        getUser(user.getId());
        userStorage.updateUser(user);
        return user;
    }

    public List<User> getUsers() {
        return new ArrayList<>(userStorage.getUsers().values());
    }

    public void removeUser(long userId) {
        getUser(userId);
        userStorage.removeUser(userId);
    }

    public User addFriend(long userId, long friendId) {
        User userFriend = getUser(friendId);
        User user = getUser(userId);
        user.addFriend(friendId);
        userStorage.updateUser(user);
        return user;
    }

    public User removeFriend(long userId, long friendId) {
        User user = getUser(userId);
        User userFriend = getUser(friendId);
        if (!user.getFriends().contains(friendId))
            throw new NotFoundException("список друзей отсутствует");
        user.removeFriend(friendId);
        userStorage.updateUser(user);

        return user;
    }

    public List<User> getFriends(long userId) {
        List<User> friendsList = new ArrayList<>();
        Set<Long> userFriends = getUser(userId).getFriends();
        for (Long friendId : userFriends) {
            friendsList.add(getUser(friendId));
        }
        return friendsList;
    }

    public List<User> getCommonFriends(long userId, long friendId) {
        List<User> userFriends = getFriends(userId);
        List<User> friendFriends = getFriends(friendId);
        userFriends.retainAll(friendFriends);
        return userFriends;
    }

    private void validate(User user) {
        if (LocalDate.parse(user.getBirthday()).isAfter(LocalDate.now()))
            throw new ValidationException("неправильный пользователь");
        if (user.getLogin().isEmpty() && user.getLogin().contains(" "))
            throw new ValidationException("неправильный пользователь");
        if (user.getName() == null || user.getName().isBlank()) user.setName(user.getLogin());

    }
}
