package ru.yandex.practicum.filmorate.storage.impl.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.LikesStorage;

import java.util.ArrayList;
import java.util.List;

@Component
public class LikesDBStorage implements LikesStorage {
    private static final String GET_FILM_ID_BY_USER_ID = "SELECT film_id FROM film_likes where user_id = ?";
    private static final String GET_USER_ID_BY_FILM_ID = "SELECT user_id FROM film_likes where film_id = ?";
    private final JdbcTemplate jdbcTemplate;

    public LikesDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Integer> getFilmIdByUserId(int userId) {
        return jdbcTemplate.query(GET_FILM_ID_BY_USER_ID, likesRowMapper("film_id"), userId).stream().findFirst().orElse(new ArrayList<>());
    }

    @Override
    public List<Integer> getUserIdByFilmId(int filmId) {
        return jdbcTemplate.query(GET_USER_ID_BY_FILM_ID, likesRowMapper("user_id"), filmId).stream().findFirst().orElse(new ArrayList<>());
    }

    private RowMapper<List<Integer>> likesRowMapper(String columnLabel) {
        return (rs, rowNum) -> {
            List<Integer> films = new ArrayList<>();
            do {
                films.add(rs.getInt(columnLabel));
            } while (rs.next());

            return films;
        };
    }
}
