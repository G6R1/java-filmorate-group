package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.UserFriendStorage;
import ru.yandex.practicum.filmorate.model.UserFriends;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class UserFriendDbStorage implements UserFriendStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserFriendDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private UserFriends makeUserFriends(ResultSet rs) throws SQLException {
        return new UserFriends(
                rs.getLong("user_id")
                , rs.getLong("friend_id")
                , rs.getBoolean("friend_status"));
    }

    public Set<Long> getUserFriends(long userId) {
        String sql = "select * from user_friends where user_id = ?";
        List<UserFriends> friends = jdbcTemplate.query(sql, (rs, rowNum) -> makeUserFriends(rs), userId);
        Set<Long> trueFriends = new HashSet<>();
        for (UserFriends userFriends : friends)
            //if (userFriends.isFriendStatus())
            trueFriends.add(userFriends.getFriendId());
        return trueFriends;
    }

    public void addUserFriend(long userId, Set<Long> userFriends) {
        for (Long friendId : userFriends) {
            String sql = "insert into user_friends(user_id, friend_id) " + "values (?, ?)";
            jdbcTemplate.update(sql, userId, friendId);
        }
    }

    public void removeUserFriend(Long userId) {
        String sql = "delete from user_friends where user_id = ?";
        jdbcTemplate.update(sql, userId);
    }
}