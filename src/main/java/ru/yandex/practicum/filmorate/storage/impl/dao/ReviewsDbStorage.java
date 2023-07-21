package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewsStorage;
import ru.yandex.practicum.filmorate.validators.ReviewValidation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component()
public class ReviewsDbStorage implements ReviewsStorage {
    private static final String GET_ALL_REVIEWS = "SELECT reviewId, content, isPositive, userId, filmId, useful FROM reviews LIMIT ";
    private static final String UPDATE_REVIEW = "UPDATE reviews SET content = ?, isPositive = ? WHERE reviewId = ?";
    private static final String DELETE_REVIEW = "DELETE FROM reviews WHERE reviewId = ?";
    private static final String GET_REVIEW_BY_ID = "SELECT reviewId, content, isPositive, userId, filmId, useful FROM reviews WHERE reviewId = ";
    private static final String GET_REVIEWS_BY_FILM_ID = "SELECT reviewId, content, isPositive, userId, filmId, useful FROM reviews WHERE filmId = ";
    private static final String LIMIT = " LIMIT ";
    private static final String INCREASE_USEFUL = "UPDATE reviews SET useful = useful + 1 WHERE reviewId = ?";
    private static final String DECREASE_USEFUL = "UPDATE reviews SET useful = useful - 1 WHERE reviewId = ?";

    private final JdbcTemplate jdbcTemplate;
    private final ReviewValidation validation;

    public ReviewsDbStorage(JdbcTemplate jdbcTemplate, ReviewValidation validation) {
        this.jdbcTemplate = jdbcTemplate;
        this.validation = validation;
    }

    @Override
    public List<Review> getAllReviews(int count) {
        return getReviews(GET_ALL_REVIEWS, Integer.toString(count));
    }

    @Override
    public Review addReview(Review review) {
        Number id = 0;
        validation.checkFilmAndUser(review.getFilmId(), review.getUserId());
        if (review.getFilmId() > 0 && review.getUserId() > 0) {
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(validation.requireNonNull(jdbcTemplate.getDataSource(), "Add review error"))
                    .withTableName("reviews")
                    .usingGeneratedKeyColumns("reviewId");
            Map<String, String> params = Map.of("content", review.getContent(), "isPositive", review.getIsPositive().toString(),
                    "userId", review.getUserId().toString(), "filmId", review.getFilmId().toString(), "useful", "0");

            id = simpleJdbcInsert.executeAndReturnKey(params);
        }
        return getReviewById((int)id);
    }

    @Override
    public Review updateReview(Review review) {
        validation.checkFilmAndUser(review.getFilmId(), review.getUserId());
        jdbcTemplate.update(UPDATE_REVIEW,
                review.getContent(), review.getIsPositive(), review.getReviewId()
                );
        return getReviewById(review.getReviewId());
    }

    @Override
    public Review deleteReview(int id) {
        Review review = getReviewById(id);
        jdbcTemplate.update(DELETE_REVIEW, id);
        return review;
    }

    @Override
    public Review getReviewById(int id) {
        Optional<Review> reviewsOpt = jdbcTemplate.query(GET_REVIEW_BY_ID + id, (rs, rowNum) -> createReview(rs)).stream().findFirst();
        return reviewsOpt.orElseThrow(() -> new ReviewNotFoundException("Отзыва с id: " + id + " нет"));
    }

    @Override
    public List<Review> getReviewsByFilmId(int id, int count) {
        validation.checkFilm(id);
        return getReviews(GET_REVIEWS_BY_FILM_ID + id + LIMIT, Integer.toString(count));
   }

    @Override
    public Review increaseUseful(int reviewId, int userId) {
        validation.checkUserAndReview(userId, reviewId);
        jdbcTemplate.update(INCREASE_USEFUL, reviewId);
        return getReviewById(reviewId);
    }

    @Override
    public Review decreaseUseful(int reviewId, int userId) {
        validation.checkUserAndReview(userId, reviewId);
        jdbcTemplate.update(DECREASE_USEFUL, reviewId);
        return getReviewById(reviewId);
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
        return reviewsOpt.orElse(new ArrayList<>());
    }

    private Review createReview(ResultSet rs) {
        try {
            return new Review(
                    rs.getInt("reviewId"), rs.getString("content"),
                    rs.getBoolean("isPositive"), rs.getInt("userId"),
                    rs.getInt("filmId"), rs.getInt("useful")
                    );
        } catch (SQLException e) {
            throw new ReviewNotFoundException("Ошибка в БД.");
        }
    }
}
