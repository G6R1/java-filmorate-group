package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventStorage {
    Long createEvent(Event event);

    List<Event> getUserEvents(Long userId);
}