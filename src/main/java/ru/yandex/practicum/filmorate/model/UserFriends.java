package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public
class UserFriends {
    private long userId;
    private long friendId;
    private boolean friendStatus;

    public UserFriends(long userId, long friendId, boolean friendStatus) {
        this.userId = userId;
        this.friendId = friendId;
        this.friendStatus = friendStatus;
    }
}
