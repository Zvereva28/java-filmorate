package ru.yandex.practicum.filmorate.validators;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ReviewException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ReviewValidation {
    private static final String GET_ALL_FILM_IDS = "SELECT id FROM films";
    private static final String GET_ALL_USER_IDS = "SELECT id FROM users";

    private final JdbcTemplate jdbcTemplate;

    public ReviewValidation(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void checkUserAndFilm(int filmId, int userId) {
        if (!getIds(GET_ALL_FILM_IDS, "film_id", filmId).contains(filmId)) {
            throw new FilmNotFoundException("Фильма с id = " + filmId + " не существует");
        }
        if (!getIds(GET_ALL_USER_IDS, "user_id", userId).contains(userId)) {
            throw new UserNotFoundException("Юзера с id = " + userId + " не существует");
        }
    }

    public <T> T requireNonNull(T obj, String message) {
        if (obj == null)
            throw new ReviewException(message);
        return obj;
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
}
