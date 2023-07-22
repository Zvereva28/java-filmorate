package ru.yandex.practicum.filmorate.validators;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;

@Component
public class FeedValidator {
    private final JdbcTemplate jdbcTemplate;
    private static final String CHECK_USER = "SELECT COUNT(id) FROM users WHERE id = ";
    private static final String CHECK_EVENT = "SELECT COUNT(event_id) FROM feed WHERE event_id = ";

    public FeedValidator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void checkUser(int id) {
        Integer count = jdbcTemplate.queryForObject(
                CHECK_USER + id, Integer.class);
        if (count == null || count == 0) {
            throw new UserNotFoundException("Юзера с id = " + id + " нет");
        }
    }

}
