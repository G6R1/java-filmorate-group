package ru.yandex.practicum.filmorate.storage;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void createUser(User user) {
        String sql = "insert into user_user(user_name, user_login, user_email, user_birthday) " +
                "values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"user_id"});
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getBirthday());
            return stmt;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
    }

    @Override
    public Optional<User> findUserById(long userId) {
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
                "user_name = ?, user_login = ?, user_email = ?, user_birthday = ? " +
                "where user_id = ?";
        jdbcTemplate.update(sql
                , user.getName()
                , user.getLogin()
                , user.getEmail()
                , user.getBirthday()
                , user.getId());
    }

    @Override
    public void removeUser(long userId) {
        String sqlQuery = "DELETE FROM USER_USER WHERE USER_ID = ?";
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
        return user;
    }
}
