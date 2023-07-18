package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewsStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component()
public class ReviewsDbStorage implements ReviewsStorage {
    private static final String GET_ALL_REVIEWS = "SELECT * FROM reviews";
    private static final String ADD_REVIEW = "INSERT INTO reviews(content, isPositive, userId, filmId) VALUES(?, ?, ?, ?);";
    private static final String UPDATE_REVIEW = "UPDATE reviews SET content = ?, isPositive = ?, useful = ? WHERE reviewId = ?;";
    private static final String DELETE_REVIEW = "UPDATE FROM reviews WHERE reviewId = ?;";
    private static final String GET_REVIEW_BY_ID = "SELECT * FROM reviews WHERE reviewId = ";
    private static final String GET_REVIEWS_BY_FILM_ID = "SELECT * FROM reviews WHERE filmId = ";
    private static final String GET_REVIEWS_BY_FILM_ID_LIMIT = " LIMIT = ";
    private static final String INCREASE_USEFUL = "UPDATE reviews SET useful = useful + 1 WHERE reviewId = ?;";
    private static final String DECREASE_USEFUL = "UPDATE reviews SET useful = useful - 1 WHERE reviewId = ?;";
    private static final String GET_ALL_FILM_IDS = "SELECT id FROM films;";
    private static final String GET_ALL_USER_IDS = "SELECT id FROM users;";
    private static final String GET_MAX_ID = "SELECT max(reviewId) as maxId FROM reviews;";

    private final JdbcTemplate jdbcTemplate;

    public ReviewsDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Review> getAllReviews() {
        return getReviews(GET_ALL_REVIEWS, "");
    }

    @Override
    public Review addReview(Review review) {
        checkUserAndFilm(review.getFilmId(), review.getUserId());
        int newId = getMaxId() + 1;
        jdbcTemplate.update(ADD_REVIEW, review.getContent(), review.getIsPositive(), review.getUserId(), review.getFilmId());

        return getReviewById(newId);
    }

    @Override
    public Review updateReview(Review review) {
        checkUserAndFilm(review.getFilmId(), review.getUserId());
        jdbcTemplate.update(UPDATE_REVIEW,
                review.getContent(), review.getIsPositive(), review.getUseful(), review.getReviewId()
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
        System.out.println(count);
        if (count == 0) {
            return getReviews(GET_REVIEWS_BY_FILM_ID, Integer.toString(id));
        } else {
            return getReviews(GET_REVIEWS_BY_FILM_ID + GET_REVIEWS_BY_FILM_ID_LIMIT + count, Integer.toString(id));
        }
    }

    @Override
    public Review increaseUseful(int reviewId, int userId) {
        int id = jdbcTemplate.update(INCREASE_USEFUL, reviewId);
        return getReviewById(id);
    }

    @Override
    public Review decreaseUseful(int reviewId, int userId) {
        int id = jdbcTemplate.update(DECREASE_USEFUL, reviewId);
        return getReviewById(id);
    }

    private List<Review> getReviews(String sql, String param) {
        List<Review> reviews = new ArrayList<>();
        Optional<List<Review>> reviewsOpt = jdbcTemplate.query(sql + param, (rs, rowNum) -> {
            do {
                reviews.add(createReview(rs));
            } while (rs.next());
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

    private List<Integer> getIds(String sql, String idName, int id) {
        List<Integer> filmIds = new ArrayList<>();
        jdbcTemplate.query(sql, (rs, rowNum) -> {
            do {
                filmIds.add(rs.getInt("id"));
            } while (rs.next());
            return filmIds;
        }).stream().findFirst().orElseThrow(() -> new FilmNotFoundException(idName + " с id = " + id + " не существует"));
        return filmIds;
    }

    private void checkUserAndFilm(int filmId, int userId){
        if (!getIds(GET_ALL_FILM_IDS, "film_id", filmId).contains(filmId)) {
            throw new FilmNotFoundException("Фильма с id = " + filmId + " не существует");
        }
        if (!getIds(GET_ALL_USER_IDS, "user_id", userId).contains(userId)) {
            throw new FilmNotFoundException("Юзера с id = " + userId + " не существует");
        }
    }

    private int getMaxId() {
        Integer maxId = jdbcTemplate.queryForObject(GET_MAX_ID, (rs, rowNum) -> rs.getInt("maxId"));
        if (maxId != null) {
            return maxId;
        }
        return 0;
    }
}
