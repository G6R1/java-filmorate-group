package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.EventStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EventDbStorage implements EventStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public EventDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Метод для внесения нового event в БД
     * @param event
     * @return автоматически сгенерированный БД идентификатор нового event
     */
    @Override
    public Long createEvent(Event event) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("events")
                .usingGeneratedKeyColumns("event_id");

        return simpleJdbcInsert.executeAndReturnKey(toMap(event)).longValue();
    }

    /**
     * Для работы метода SimpleJdbcInsert.executeAndReturnKey
     *
     * @param event
     * @return
     */
    private Map<String, Object> toMap(Event event) {
        Map<String, Object> values = new HashMap<>();
        values.put("user_id", event.getUserId());
        values.put("event_type", event.getEventType());
        values.put("operation", event.getOperation());
        values.put("event_time", event.getTimestamp());
        values.put("entity_id", event.getEntityId());
        return values;
    }

    @Override
    public List<Event> getUserEvents(Long userId) {
        String sql = "select * from events where user_id = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeEvent(rs), userId);
    }

    /**
     * Маппер
     */
    private Event makeEvent(ResultSet rs) throws SQLException {
        Long eventId = rs.getLong("event_id");
        Long userId = rs.getLong("user_id");
        String eventType = rs.getString("event_type"); // одно из значениий LIKE, REVIEW или FRIEND
        String operation = rs.getString("operation"); // одно из значениий REMOVE, ADD, UPDATE
        Long timestamp = rs.getLong("event_time");
        Long entityId = rs.getLong("entity_id");

        return new Event(eventId, userId, eventType, operation, timestamp, entityId);
    }

    /**
     * Дополнительный метод для извлечения id из таблицы user_friends (необходимо для создания объекта Event),
     * т.к. логика работы механизма добавления и удаления друзей не позволяет в процессе извлечь эту
     * информацию.
     * @return
     */
    @Override
    public Long getFriendshipEntityId(Long userId, Long friendId){
        String sql = "select friendship_id from user_friends where user_id = ? and friend_id = ?";
        SqlRowSet friendshipIdRow = jdbcTemplate.queryForRowSet(sql, userId, friendId);

        if (friendshipIdRow.next()) {
            return friendshipIdRow.getLong("friendship_id");
        } else {
            throw new RuntimeException("Ошибка при извлечении friendship_id.");
        }
    }

    /**
     * Дополнительный метод для извлечения rate_id из таблицы rate_users (необходимо для создания объекта Event)
     * @param userId
     * @param filmId
     * @return
     */
    @Override
    public Long getRateEntityId(Long userId, Long filmId) {
        String sql = "select rate_id from rate_users where user_id = ? and film_id = ?";
        SqlRowSet rateIdRow = jdbcTemplate.queryForRowSet(sql, userId, filmId);

        if (rateIdRow.next()) {
            return rateIdRow.getLong("rate_id");
        } else {
            throw new RuntimeException("Ошибка при извлечении rate_id.");
        }
    }
}
