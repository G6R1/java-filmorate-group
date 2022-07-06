package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.ReviewStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.*;

@Service
public class ReviewService {
    private FilmService filmService;
    private ReviewStorage reviewStorage;
    private UserService userService;

    public ReviewService(FilmService filmService, ReviewStorage reviewStorage, UserService userService) {
        this.filmService = filmService;
        this.reviewStorage = reviewStorage;
        this.userService = userService;
    }

    public Review addReview(Review review) {
        validate(review);
        reviewStorage.createReview(review);
        return review;
    }

    public Review updateReview(Review review) {
        validate(review);
        reviewStorage.updateReview(review);
        return getReviewById(review.getId());
    }

    public void updateReviewLike(Long id, Long userId, Review review) {
        userService.getUser(userId);
        validate(review);
        review = getReviewById(id);
        int useful = review.getUseful() + 1;
        reviewStorage.setUseful(useful, id);
    }

    public void updateReviewDislike(Long id, Long userId, Review review) {
        userService.getUser(userId);
        validate(review);
        review = getReviewById(id);
        int useful = review.getUseful() - 1;
        reviewStorage.setUseful(useful, id);
    }

    public void removeReviewById(Long id) {
        getReviewById(id);
        reviewStorage.removeReview(id);
    }

    public Review getReviewById(Long id) {
        return reviewStorage.getReviewById(id)
                .orElseThrow(() -> new NotFoundException("такого отзыва нет в списке"));
    }

    public Collection<Review> getAllReviewsByFilmId(Long filmId, Long count) {
        List<Review> getAll;
        if (filmId != null) {
            filmService.getFilm(filmId);
            getAll = reviewStorage.getAllReviewsByFilmId(filmId, count);
        } else getAll = reviewStorage.getAllReviews(count);
        getAll.sort(Review.COMPARE_BY_USEFUL);
        return getAll;
    }

    private void validate(Review review) {
        userService.getUser(review.getUserId());
        filmService.getFilm(review.getFilmId());
        if (review.getIsPositive() == null)
            throw new ValidationException("Не указана оценка");
    }
}