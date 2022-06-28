package ru.yandex.practicum.filmorate.dao;

import java.util.Set;

public interface UserFriendStorage {

    Set<Long> getUserFriends(long userId);

    void addUserFriend(long userId, Set<Long> userFriends);

    void removeUserFriend(Long userId);
}