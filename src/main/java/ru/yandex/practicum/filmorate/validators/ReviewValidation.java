package ru.yandex.practicum.filmorate.validators;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ReviewException;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ReviewValidation {
    private static final String GET_ALL_FILM_IDS = "SELECT id FROM films";
    private static final String GET_ALL_USER_IDS = "SELECT id FROM users";
    private static final String GET_ALL_REVIEW_IDS = "SELECT reviewId FROM reviews";

    private final JdbcTemplate jdbcTemplate;

    public ReviewValidation(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void checkFilm(int filmId) {
        if (!getIds(GET_ALL_FILM_IDS, "film_id", filmId).contains(filmId)) {
            throw new FilmNotFoundException("Фильма с id = " + filmId + " не существует");
        }
    }

    public void checkFilmAndUser(int filmId, int userId) {
        if (!getIds(GET_ALL_FILM_IDS, "film_id", filmId).contains(filmId)) {
            throw new FilmNotFoundException("Фильма с id = " + filmId + " не существует");
        }
        if (!getIds(GET_ALL_USER_IDS, "user_id", userId).contains(userId)) {
            throw new UserNotFoundException("Юзера с id = " + userId + " не существует");
        }
    }

    public void checkUserAndReview(int userId, int reviewId) {
        if (!getIds(GET_ALL_USER_IDS, "user_id", userId).contains(userId)) {
            throw new UserNotFoundException("Юзера с id = " + userId + " не существует");
        }
        if (!getIds(GET_ALL_REVIEW_IDS, "reviewId", reviewId).contains(reviewId)) {
            throw new ReviewNotFoundException("Отзыва с id = " + reviewId + " не существует");
        }
    }

    public <T> T requireNonNull(T obj, String message) {
        if (obj == null)
            throw new ReviewException(message);
        return obj;
    }

    private List<Integer> getIds(String sql, String idName, int id) {
        List<Integer> ids = new ArrayList<>();
        String columnName;
        if (idName.equals("reviewId")) {
            columnName = "reviewId";
        } else {
            columnName = "id";
        }
        jdbcTemplate.query(sql, (rs, rowNum) -> {
            do {
                ids.add(rs.getInt(columnName));
            } while (rs.next());
            return ids;
        }).stream().findFirst().orElseThrow(() -> new ReviewException(idName + " с id = " + id + " не существует"));
        return ids;
    }
}
