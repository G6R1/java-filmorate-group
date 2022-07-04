package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public interface ReviewStorage {

    //Добавление нового отзыва
    Review add(Review review);

    Review update(Review review);

    void remove(Long id);

    Review getReviewById(Long id);

    Collection<Review> getAllReviewsByFilmId(Long filmId, Long count);

    //Добавить лайк/дизлайк к отзыву
    Review addReviewUseful(Long reviewId, Long userId, Long value);

    //Значение value:
    // 1 - лайк
    // -1 - дизлайк

    //Удалить лайк/дизлайк к отзыву
    Review removeReviewUseful(Long reviewId, Long userId, Long value);
}
