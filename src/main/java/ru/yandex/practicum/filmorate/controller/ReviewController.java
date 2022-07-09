package ru.yandex.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/{id}")
    public Optional<Review> findById(@PathVariable Long id) {
        return reviewService.getReviewById(id);
    }


    @GetMapping
    public Collection<Review> getAllReviewsByFilmId(
            @RequestParam(required = false) Long filmId,
            @RequestParam(defaultValue = "10", required = false) Long count) {

        return reviewService.getAllReviews(filmId, count);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Review addReview(@Valid @RequestBody Review review) {
        return reviewService.addReview(review);
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeReviewById(@PathVariable Long id) {
        reviewService.removeReviewById(id);
    }


    @PutMapping("/{id}/like/{userId}")
    public Optional<Review> putLikeReview(
            @PathVariable Long id,
            @PathVariable Long userId) {
        return reviewService.addLikeReview(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public Optional<Review> putDislikeReview(
            @PathVariable Long id,
            @PathVariable Long userId) {
        return reviewService.addDislikeReview(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Optional<Review> deleteLikeReview(
            @PathVariable Long id,
            @PathVariable Long userId) {
        return reviewService.removeLikeReview(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public Optional<Review> deleteDislikeReview(
            @PathVariable Long id,
            @PathVariable Long userId) {
        return reviewService.removeDislikeReview(id, userId);
    }
}
