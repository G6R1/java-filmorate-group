package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserFriends;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component("UserDbStorage")
@Primary
@Qualifier("UserDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void createUser(User user) {
        String sql = "insert into user_user(user_login, user_name, user_email, user_birthday) " +
                "values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"user_id"});
            stmt.setString(1, user.getLogin());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getBirthday());
            return stmt;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        if (user.getFriends() != null) {
            addUserFriend(user.getId(), user.getFriends());
        }

    }

    @Override
    public Optional<User> getUser(long userId) {
        String sql = "select * from user_user where user_id = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, this::makeUser, userId);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void updateUser(User user) {
        String sql = "update user_user set " +
                "user_login = ?, user_name = ?, user_email = ?, user_birthday = ? " +
                "where user_id = ?";
        jdbcTemplate.update(sql
                , user.getLogin()
                , user.getName()
                , user.getEmail()
                , user.getBirthday()
                , user.getId());
        removeUserFriend(user.getId());
        if (user.getFriends() != null)
            addUserFriend(user.getId(), user.getFriends());
    }

    @Override
    public void removeUser(long userId) {
        String sqlQuery = "delete from user_user where id = ?";
        jdbcTemplate.update(sqlQuery, userId);
    }

    @Override
    public Map<Long, User> getUsers() {
        String sqlQuery = "select * from user_user";
        return jdbcTemplate.query(sqlQuery, this::makeUser)
                .stream().collect(Collectors.toMap(User::getId, item -> item));
    }

    private User makeUser(ResultSet resultSet, int rowNum) throws SQLException {
        User user = new User();
        user.setName(resultSet.getString("user_name"));
        user.setLogin(resultSet.getString("user_login"));
        user.setId(resultSet.getLong("user_id"));
        user.setEmail(resultSet.getString("user_email"));
        user.setBirthday(resultSet.getString("user_birthday"));
        if (!getUserFriends(user.getId()).isEmpty())
            user.setFriends(getUserFriends(user.getId()));
        return user;
    }

    private UserFriends makeUserFriends(ResultSet rs) throws SQLException {
        return new UserFriends(
                rs.getLong("user_id")
                , rs.getLong("friend_id")
                , rs.getBoolean("friend_status"));
    }

    private Set<Long> getUserFriends(long userId) {
        String sql = "select * from user_friends where user_id = ?";
        List<UserFriends> friends = jdbcTemplate.query(sql, (rs, rowNum) -> makeUserFriends(rs), userId);
        Set<Long> trueFriends = new HashSet<>();
        for (UserFriends userFriends : friends)
            //if (userFriends.isFriendStatus())
            trueFriends.add(userFriends.getFriendId());
        return trueFriends;
    }

    private void addUserFriend(long userId, Set<Long> userFriends) {
        for (Long friendId : userFriends) {
            String sql = "insert into user_friends(user_id, friend_id) " + "values (?, ?)";
            jdbcTemplate.update(sql, userId, friendId);
        }
    }

    private void removeUserFriend(Long userId) {
        String sql = "delete from user_friends where user_id = ?";
        jdbcTemplate.update(sql, userId);
    }
}