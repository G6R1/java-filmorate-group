package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventStorage {
    Long createEvent (Event event) ;

    List<Event> getUserEvents (Long userId) ;

    /**
     * Дополнительный метод для извлечения friendship_id из таблицы user_friends (необходимо для создания объекта Event),
     * т.к. логика работы механизма добавления и удаления друзей не позволяет в процессе извлечь эту информацию.
     * @param userId
     * @param friendId
     * @return
     */
    Long getFriendshipEntityId(Long userId, Long friendId) ;

    /**
     * Дополнительный метод для извлечения rate_id из таблицы rate_users (необходимо для создания объекта Event)
     * @param userId
     * @param filmId
     * @return
     */
    Long getRateEntityId(Long userId, Long filmId) ;
}
