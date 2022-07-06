package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {

    //Добавление нового отзыва
    void createReview(Review review);

    void updateReview(Review review);

    void removeReview(Long id);

    void setUseful(int useful, Long id);

    Optional<Review> getReviewById(Long id);

    List<Review> getAllReviewsByFilmId(Long filmId, Long count);

    List<Review> getAllReviews(Long count);
}