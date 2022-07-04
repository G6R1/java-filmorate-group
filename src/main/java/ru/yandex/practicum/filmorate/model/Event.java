package ru.yandex.practicum.filmorate.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Instant;

@Getter
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class Event {
        private Long eventId;
        private final Long userId;
        private final String eventType; // одно из значениий LIKE, REVIEW или FRIEND
        private final String operation; // одно из значениий REMOVE, ADD, UPDATE
        private final Long timestamp;
        private final Long entityId;

        public Event(Long eventId, Long userId, String eventType, String operation, Long timestamp, Long entityId) {
                this.eventId = eventId;
                this.userId = userId;
                this.eventType = eventType;
                this.operation = operation;
                this.timestamp = timestamp;
                this.entityId = entityId;
        }
}
