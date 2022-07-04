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
        private final int eventId;
        private final int userId;
        private final String eventType; // одно из значениий LIKE, REVIEW или FRIEND
        private final String operation; // одно из значениий REMOVE, ADD, UPDATE
        private final Instant timestamp;
        private final int entityId;
}
