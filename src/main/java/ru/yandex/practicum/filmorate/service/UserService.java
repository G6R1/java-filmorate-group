package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.InternalErrorException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

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
        return userStorage.getUser(userId)
                .orElseThrow(() -> new NotFoundException("такого пользователя нет в списке"));
    }

    public User createUser(User user) {
        if (validate(user) || userStorage.getUsers().containsKey(user.getId()))
            throw new ValidationException("неправильный пользователь");
        userStorage.createUser(user);
        return user;
    }

    public User updateUser(User user) {
        if (validate(user) || !userStorage.getUsers().containsKey(user.getId()))
            throw new NotFoundException("такого пользователя нет в списке");
        userStorage.updateUser(user);
        return user;
    }

    public List<User> getUsers() {
        List<User> userList = new ArrayList<>(0);
        for (Long key : userStorage.getUsers().keySet()) {
            User user = userStorage.getUsers().get(key);
            userList.add(user);
        }
        return userList;
    }

    public void removeUser(long userId) {
        getUser(userId);
        userStorage.removeUser(userId);
    }

    public User addFriend(long userId, long friendId) {
        User userFriend = getUser(friendId);
        User user = getUser(userId);
        userFriend.addFriend(userId);
        userStorage.updateUser(userFriend);
        user.addFriend(friendId);
        userStorage.updateUser(user);
        return user;
    }

    public User removeFriend(long userId, long friendId) {
        User user = getUser(userId);
        User userFriend = getUser(friendId);
        if (user.getFriends().isEmpty() || userFriend.getFriends().isEmpty())
            throw new InternalErrorException("список друзей отсутствует");
        user.removeFriend(friendId);
        userFriend.removeFriend(userId);
        userStorage.updateUser(user);
        userStorage.updateUser(userFriend);
        return user;
    }

    public List<User> getFriends(long userId) {
        List<User> friendsList = new ArrayList<>();
        Set<Long> userFriends = getUser(userId).getFriends();
        for (Long integer : userFriends) {
            friendsList.add(getUser(integer));
        }
        return friendsList;
    }

    public List<User> getCommonFriends(long userId, long friendId) {
        List<User> userFriends = getFriends(userId);
        List<User> friendFriends = getFriends(friendId);
        userFriends.retainAll(friendFriends);
        return userFriends;
    }

    private boolean validate(User user) {
        if (LocalDate.parse(user.getBirthday()).isAfter(LocalDate.now())) return true;
        if (user.getLogin().isEmpty() && user.getLogin().contains(" ")) return true;
        if (user.getName() == null || user.getName().isBlank()) user.setName(user.getLogin());
        return false;
    }
}