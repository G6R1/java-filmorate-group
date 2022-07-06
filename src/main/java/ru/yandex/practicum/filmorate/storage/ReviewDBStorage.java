package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.ReviewStorage;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@Slf4j
public class ReviewDBStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    public ReviewDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void createReview(Review review) {
        String sql = "insert into reviews (content, is_positive, user_id, film_id)" +
                " values (?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"review_id"});
            stmt.setString(1, review.getContent());
            stmt.setBoolean(2, review.getIsPositive());
            stmt.setLong(3, review.getUserId());
            stmt.setLong(4, review.getFilmId());
            return stmt;
        }, keyHolder);
        review.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        log.info("Добавлен отзыв: {}", review);
    }

    @Override
    public void updateReview(Review review) {
        String sql = "update reviews set content = ?," +
                " is_positive = ?" +
                "WHERE review_id = ?";
        jdbcTemplate.update(sql,
                review.getContent(),
                review.getIsPositive(),
                review.getId());
        log.info("Изменён отзыв: {}", review);
    }

    @Override
    public void removeReview(Long id) {
        jdbcTemplate.update("delete from reviews where review_id = ?", id);
        log.info("Удалён отзыв: {}", id);
    }

    @Override
    public void setUseful(int useful, Long id) {
        String sql = "update reviews set useful = ? WHERE review_id = ?";
        jdbcTemplate.update(sql, useful, id);
    }

    @Override
    public Optional<Review> getReviewById(Long id) {
        String sql = "select * from reviews where review_id = ?";
        try {
            Review review = jdbcTemplate.queryForObject(sql, this::makeReview, id);
            return Optional.ofNullable(review);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Review> getAllReviewsByFilmId(Long filmId, Long count) {
        String sqlQuery = "select * from reviews where film_id = ? LIMIT ?;";
        return jdbcTemplate.query(sqlQuery, this::makeReview, filmId, count);
    }

    @Override
    public List<Review> getAllReviews(Long count) {
        String sqlQuery = "select * from reviews LIMIT ?;";
        return jdbcTemplate.query(sqlQuery, this::makeReview, count);
    }

    private Review makeReview(ResultSet resultSet, int rowNum) throws SQLException {
        Review review = new Review();
        review.setId(resultSet.getLong("review_id"));
        review.setContent(resultSet.getString("content"));
        review.setIsPositive(resultSet.getBoolean("is_positive"));
        review.setUserId(resultSet.getLong("user_id"));
        review.setFilmId(resultSet.getLong("film_id"));
        review.setUseful(resultSet.getInt("useful"));
        return review;
    }
}