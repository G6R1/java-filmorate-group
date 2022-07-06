package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.ReviewStorage;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final UserService userService;
    private final FilmService filmService;
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

    public Optional<Review> getReviewById(Long id) {
        return reviewStorage.getReviewById(id);
    }

    public Collection<Review> getAllReviews(Long id, Long count) {
        return reviewStorage.getAllReviewsByFilmId(id, count);
    }

    public Optional<Review> addLikeReview(Long reviewId, Long userId) {
        return reviewStorage.addReviewUseful(reviewId, userId, USEFUL_CHANGE_STEP);
    }

    public Optional<Review> addDislikeReview(Long reviewId, Long userId) {
        return reviewStorage.addReviewUseful(reviewId, userId, -USEFUL_CHANGE_STEP);
    }

    public Optional<Review> removeLikeReview(Long reviewId, Long userId) {
        return reviewStorage.removeReviewUseful(reviewId, userId, USEFUL_CHANGE_STEP);
    }

    public Optional<Review> removeDislikeReview(Long reviewId, Long userId) {
        return reviewStorage.removeReviewUseful(reviewId, userId, -USEFUL_CHANGE_STEP);
    }

    private void validate(Review review) {
        userService.getUser(review.getUserId());
        filmService.getFilm(review.getFilmId());
        if (review.getIsPositive() == null)
            throw new ValidationException("Не указана оценка");
    }
}