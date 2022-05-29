package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.InternalErrorException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

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

    public User addFriend(int userId, int friendId) {
        User userFriend = userStorage.getUser(friendId);
        User user = userStorage.getUser(userId);
        userFriend.addFriend(userId);
        userStorage.updateUser(userFriend);
        user.addFriend(friendId);
        userStorage.updateUser(user);
        return user;
    }

    public User removeFriend(int userId, int friendId) {
        User user = userStorage.getUser(userId);
        User userFriend = userStorage.getUser(friendId);
        if (user.getFriends() == null || userFriend.getFriends() == null) {
            throw new InternalErrorException("список друзей отсутствует");
        } else {
            user.removeFriend(friendId);
            userFriend.removeFriend(userId);
            userStorage.updateUser(user);
            userStorage.updateUser(userFriend);
        }
        return user;
    }

    public List<User> getFriends(int userId) {
        List<User> friendsList = new ArrayList<>();
        if (userStorage.getUser(userId).getFriends() == null) {
            throw new InternalErrorException("список друзей отсутствует");
        } else {
            Set<Integer> userFriends = userStorage.getUser(userId).getFriends();
            for (Integer integer : userFriends) {
                friendsList.add(userStorage.getUser(integer));
            }
        }
        return friendsList;
    }

    public List<User> getCommonFriends(int userId, int friendId) {
        if (userStorage.getUser(userId).getFriends() == null || userStorage.getUser(friendId).getFriends() == null) {
            throw new InternalErrorException("список друзей отсутствует");
        }
        List<User> userFriends = getFriends(userId);
        List<User> friendFriends = getFriends(friendId);
        userFriends.retainAll(friendFriends);
        return userFriends;
    }
}