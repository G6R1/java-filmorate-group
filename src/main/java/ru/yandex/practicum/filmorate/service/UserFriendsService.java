package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserFriendStorage;

import java.util.Set;

@Service
public class UserFriendsService {
    private final UserFriendStorage userFriendStorage;

    public UserFriendsService(UserFriendStorage userFriendStorage) {
        this.userFriendStorage = userFriendStorage;
    }

    Set<Long> getUserFriends(long userId) {
        return userFriendStorage.getUserFriends(userId);
    }

    void addUserFriend(long userId, Set<Long> userFriends) {
        userFriendStorage.addUserFriend(userId, userFriends);
    }

    void removeUserFriend(Long userId) {
        userFriendStorage.removeUserFriend(userId);
    }
}