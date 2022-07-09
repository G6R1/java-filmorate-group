package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.EventStorage;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;

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
        EventType eventType = EventType.valueOf(rs.getString("event_type"));
        EventOperation operation = EventOperation.valueOf(rs.getString("operation"));
        Long timestamp = rs.getLong("event_time");
        Long entityId = rs.getLong("entity_id");

        return new Event(eventId, userId, eventType, operation, timestamp, entityId);
    }
}
