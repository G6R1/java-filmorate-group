package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.EventStorage;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;

import java.time.Instant;
import java.util.List;

@Service
public class EventService {
    private EventStorage eventStorage;
    private UserService userService;

    @Autowired
    public EventService(EventStorage eventStorage, @Lazy UserService userService) {
        this.eventStorage = eventStorage;
        this.userService = userService;
    }

    void createEvent(Long userId, EventType eventType, EventOperation operation, Long entityId) {
        Long timestamp = Instant.now().toEpochMilli();
        eventStorage.createEvent(new Event(userId, eventType, operation, timestamp, entityId));
    }

    public List<Event> getUserEvents(Long userId) {
        //проверяем, есть ли такой юзер в БД
        userService.getUser(userId);

        return eventStorage.getUserEvents(userId);
    }
}
