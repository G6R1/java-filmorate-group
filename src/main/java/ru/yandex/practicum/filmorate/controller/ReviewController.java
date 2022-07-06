package ru.yandex.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/{id}")
    public Review findById(@PathVariable Long id) {
        return reviewService.getReviewById(id);
    }

    @GetMapping
    public Collection<Review> getAllReviewsByFilmId(
            @RequestParam(required = false) Long filmId,
            @RequestParam(defaultValue = "10", required = false) Long count) {

        return reviewService.getAllReviewsByFilmId(filmId, count);
    }

    @PostMapping
    public Review addReview(@Valid @RequestBody Review review) {
        return reviewService.addReview(review);
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void removeReviewById(@PathVariable Long id) {
        reviewService.removeReviewById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void LikeReview(@Valid @RequestBody Review review,
                           @PathVariable Long id,
                           @PathVariable Long userId) {
        reviewService.updateReviewLike(id, userId, review);
    }

    @PutMapping("/{id}/dislike/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void DislikeReview(@Valid @RequestBody Review review,
                              @PathVariable Long id,
                              @PathVariable Long userId) {
        reviewService.updateReviewDislike(id, userId, review);
    }
}