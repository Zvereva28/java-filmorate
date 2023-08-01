package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmException;
import ru.yandex.practicum.filmorate.exceptions.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewsStorage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
public class ReviewsDbStorage implements ReviewsStorage {
    private static final String GET_ALL_REVIEWS = "SELECT review_id, content, is_positive, user_id, film_id, useful " +
            "FROM reviews LIMIT ";
    private static final String UPDATE_REVIEW = "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";
    private static final String DELETE_REVIEW = "DELETE FROM reviews WHERE review_id = ?";
    private static final String GET_REVIEW_BY_ID = "SELECT review_id, content, is_positive, user_id, film_id, useful " +
            "FROM reviews WHERE review_id = ";
    private static final String GET_REVIEWS_BY_FILM_ID = "SELECT review_id, content, is_positive, " +
            "user_id, film_id, useful FROM reviews WHERE film_id = ";
    private static final String LIMIT = " LIMIT ";
    private static final String INCREASE_USEFUL = "UPDATE reviews SET useful = useful + 1 WHERE review_id = ?";
    private static final String DECREASE_USEFUL = "UPDATE reviews SET useful = useful - 1 WHERE review_id = ?";

    private final JdbcTemplate jdbcTemplate;

    public ReviewsDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Review> getAllReviews(int count) {

        return getReviews(GET_ALL_REVIEWS, Integer.toString(count));
    }

    @Override
    public Review addReview(Review review) {
        try {
            SimpleJdbcInsert simpleJdbcInsert =
                    new SimpleJdbcInsert(Objects.requireNonNull(jdbcTemplate.getDataSource()))
                        .withTableName("reviews")
                        .usingGeneratedKeyColumns("review_id");
            Map<String, String> params = Map.of("content", review.getContent(), "is_positive",
                    review.getIsPositive().toString(), "user_id", review.getUserId().toString(),
                    "film_id", review.getFilmId().toString(), "useful", "0");

            Number id = simpleJdbcInsert.executeAndReturnKey(params);
            review.setReviewId(id.intValue());
        } catch (NullPointerException e) {
            throw new FilmException("Отзыв фильму id =" + review.getFilmId() + "не создан");
        }

        return review;
    }

    @Override
    public Review updateReview(Review review) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        PreparedStatement ps = connection.prepareStatement(UPDATE_REVIEW, new String[]{"review_id",
                                "content", "is_positive", "user_id", "film_id", "useful"});
                        ps.setString(1, review.getContent());
                        ps.setString(2, review.getIsPositive().toString());
                        ps.setString(3, review.getReviewId().toString());
                        return ps;
                    }
                },
                keyHolder);
        return new Review((Integer) keyHolder.getKeys().get("review_id"),
                (String) keyHolder.getKeys().get("content"),
                (Boolean) keyHolder.getKeys().get("is_positive"),
                (Integer) keyHolder.getKeys().get("user_id"),
                (Integer) keyHolder.getKeys().get("film_id"),
                (Integer) keyHolder.getKeys().get("useful"));
    }

    @Override
    public Review deleteReview(int id) {
        Review review = getReviewById(id);
        jdbcTemplate.update(DELETE_REVIEW, id);

        return review;
    }

    @Override
    public Review getReviewById(int id) {
        Optional<Review> reviewsOpt =
                jdbcTemplate.query(GET_REVIEW_BY_ID + id, (rs, rowNum) -> createReview(rs)).stream().findFirst();

        return reviewsOpt.orElseThrow(() -> new ReviewNotFoundException("Отзыва с id: " + id + " нет"));
    }

    @Override
    public List<Review> getReviewsByFilmId(int id, int count) {

        return getReviews(GET_REVIEWS_BY_FILM_ID + id + LIMIT, Integer.toString(count));
    }

    @Override
    public void increaseUseful(int reviewId, int userId) {
        int change = jdbcTemplate.update(INCREASE_USEFUL, reviewId);
        if (change == 0) {
            throw new ReviewNotFoundException("Отзыв с id = " + reviewId +
                    " у пользователя с id =" + userId + " не существует");
        }
    }

    @Override
    public void decreaseUseful(int reviewId, int userId) {
        int change = jdbcTemplate.update(DECREASE_USEFUL, reviewId);
        if (change == 0) {
            throw new ReviewNotFoundException("Отзыв с id = " + reviewId +
                    " у пользователя с id =" + userId + " не существует");
        }
    }

    private List<Review> getReviews(String sql, String param) {
        List<Review> reviews = new ArrayList<>();
        Optional<List<Review>> reviewsOpt = jdbcTemplate.query(sql + param, (rs, rowNum) -> {
            do {
                reviews.add(createReview(rs));
            } while (rs.next());
            reviews.sort(Comparator.comparingInt(Review::getUseful).reversed());

            return reviews;
        }).stream().findFirst();

        return reviewsOpt.orElse(Collections.emptyList());
    }

    private Review createReview(ResultSet rs) {
        try {
            return new Review(
                    rs.getInt("review_id"), rs.getString("content"),
                    rs.getBoolean("is_positive"), rs.getInt("user_id"),
                    rs.getInt("film_id"), rs.getInt("useful")
            );
        } catch (SQLException e) {
            throw new ReviewNotFoundException("Ошибка при сохранения отзыва о фильме в БД." + e.getMessage());
        }
    }
}
