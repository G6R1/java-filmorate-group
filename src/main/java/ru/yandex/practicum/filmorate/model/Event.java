package ru.yandex.practicum.filmorate.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class Event {
    private Long eventId;
    private final Long userId;
    private final EventType eventType; // одно из значениий LIKE, REVIEW или FRIEND
    private final EventOperation operation; // одно из значениий REMOVE, ADD, UPDATE
    private final Long timestamp;
    private final Long entityId;

    public Event(Long eventId, Long userId, EventType eventType, EventOperation operation, Long timestamp, Long entityId) {
        this.eventId = eventId;
        this.userId = userId;
        this.eventType = eventType;
        this.operation = operation;
        this.timestamp = timestamp;
        this.entityId = entityId;
    }
}
