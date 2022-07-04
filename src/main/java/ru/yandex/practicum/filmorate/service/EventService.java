package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.EventStorage;

import java.time.Instant;
import java.util.List;

@Service
public class EventService {
    EventStorage eventStorage;
    UserService userService;

    @Autowired
    public EventService(EventStorage eventStorage, UserService userService) {
        this.eventStorage = eventStorage;
        this.userService = userService;
    }

    public void createEvent(Long userId, String eventType, String operation, Long entityId) {
        Long timestamp = Instant.now().toEpochMilli();
        eventStorage.createEvent(new Event(userId, eventType, operation,timestamp,entityId));
    }

    public List<Event> getUserEvents(Long userId) {
        //проверяем, есть ли такой юзер в БД
        userService.getUser(userId);

        return eventStorage.getUserEvents(userId);
    }
}
