package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.ReviewStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReviewDBStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review add(Review review) {

        String sql = "insert into reviews (user_id, film_id, content, is_positive)" +
                " values (?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"review_id"});
            stmt.setLong(1, review.getUserId());
            stmt.setLong(2, review.getFilmId());
            stmt.setString(3, review.getContent());
            stmt.setBoolean(4, review.getIsPositive());
            return stmt;
        }, keyHolder);
        review.setReviewId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        log.info("Добавлен отзыв: {}", review);
        return review;
    }

    @Override
    public Review update(Review review) {

        String sql = "update reviews set content = ?," +
                " is_positive = ?" +
                "WHERE review_id = ?";

        jdbcTemplate.update(sql,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());

        log.info("Изменён отзыв: {}", getReviewById(review.getReviewId()));
        return review;
    }

    @Override
    public void remove(Long id) {
        jdbcTemplate.update("delete from reviews where review_id = ?", id);
        log.info("Удалён отзыв: {}", id);
    }

    @Override
    public Optional<Review> getReviewById(Long id) {
        SqlRowSet reviewRows = jdbcTemplate.queryForRowSet("select * from reviews where review_id = ?", id);
        if (reviewRows.next()) {
            Review review = new Review(
                    reviewRows.getLong("review_id"),
                    reviewRows.getLong("user_id"),
                    reviewRows.getLong("film_id"),
                    reviewRows.getString("content"),
                    reviewRows.getBoolean("is_positive"));
            log.info("Получен отзыв: {}", id);
            return Optional.of(review);
        } else {
            log.error("Не найден отзыв: {}", id);
            throw new NotFoundException("Отзыв #" + id + " не найден!");
        }
    }

    @Override
    public Collection<Review> getAllReviewsByFilmId(Long filmId, Long count) {
        Collection<Review> allReview = new ArrayList<>();
        SqlRowSet allReviewRows;
        if (filmId == null) {
            allReviewRows = jdbcTemplate.queryForRowSet("select r.review_id as rrev_id, r.user_id as rus_id, " +
                    "film_id, content, is_positive, " +
                    "(CASE WHEN sum(rr.useful) IS NULL THEN 0 ELSE sum(rr.useful) END) as sum " +
                    "from reviews as r " +
                    "left join review_rating as rr on rr.review_id = r.review_id " +
                    "group by r.review_id " +
                    "order by sum desc " +
                    "LIMIT ?", count);
        } else {
            allReviewRows = jdbcTemplate.queryForRowSet("select r.review_id as rrev_id, r.user_id as rus_id, " +
                    "film_id, content, is_positive, " +
                    "(CASE WHEN sum(rr.useful) IS NULL THEN 0 ELSE sum(rr.useful) END) as sum " +
                    "from reviews as r " +
                    "left join review_rating as rr on rr.review_id = r.review_id " +
                    "where film_id = ? " +
                    "group by r.review_id " +
                    "order by sum desc " +
                    "LIMIT ?", filmId, count);
        }
        while (allReviewRows.next()) {
            allReview.add(new Review(
                    allReviewRows.getLong("RREV_ID"),
                    allReviewRows.getLong("RUS_ID"),
                    allReviewRows.getLong("film_id"),
                    allReviewRows.getString("content"),
                    allReviewRows.getLong("SUM"),
                    allReviewRows.getBoolean("is_positive")));
        }
        return allReview;
    }

    @Override
    public Optional<Review> addReviewUseful(Long reviewId, Long userId, Long value) {
        jdbcTemplate.update("insert into review_rating (review_id, user_id, useful) " +
                "VALUES (?, ?, ?);", reviewId, userId, value);
        return getReviewById(reviewId);
    }

    @Override
    public Optional<Review> removeReviewUseful(Long reviewId, Long userId, Long value) {
        jdbcTemplate.update("delete from" +
                " review_rating" +
                " where review_id = ? " +
                "and user_id = ? and useful = ?", reviewId, userId, value);
        return getReviewById(reviewId);
    }
}