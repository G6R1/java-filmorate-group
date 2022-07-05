package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.ReviewStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private static final long USEFUL_CHANGE_STEP = 1;


    public Review addReview(Review review) {
        validate(review);
        return reviewStorage.add(review);
    }

    public Review updateReview(Review review) {
        validate(review);
        return reviewStorage.update(review);
    }

    public void removeReviewById(Long id) {
        reviewStorage.remove(id);
    }

    public Review getReviewById(Long id) {
        return reviewStorage.getReviewById(id);
    }

    public Collection<Review> getAllReviews(Long id, Long count) {
        return reviewStorage.getAllReviewsByFilmId(id, count);
    }

    public Review addLikeReview(Long reviewId, Long userId) {
        return reviewStorage.addReviewUseful(reviewId, userId, USEFUL_CHANGE_STEP);
    }

    public Review addDislikeReview(Long reviewId, Long userId) {
        return reviewStorage.addReviewUseful(reviewId, userId, -USEFUL_CHANGE_STEP);
    }

    public Review removeLikeReview(Long reviewId, Long userId) {
        return reviewStorage.removeReviewUseful(reviewId, userId, USEFUL_CHANGE_STEP);
    }

    public Review removeDislikeReview(Long reviewId, Long userId) {
        return reviewStorage.removeReviewUseful(reviewId, userId, -USEFUL_CHANGE_STEP);
    }

    private void validate(Review review) {
        if (review.getUserId() == null)
            throw new ValidationException("Не указан пользователь");
        if (review.getUserId() < 0)
            throw new NotFoundException("Пользователь не найден");
        if (review.getFilmId() == null)
            throw new ValidationException("Не указан фильм");
        if (review.getFilmId() < 0)
            throw new NotFoundException("Фильм не найден");
        if (review.getIsPositive() == null)
            throw new ValidationException("Не указана оценка");
    }
}