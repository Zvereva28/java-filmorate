package ru.yandex.practicum.filmorate.storage.impl.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.LikesStorage;

import java.util.ArrayList;
import java.util.List;

@Component
public class LikesDBStorage implements LikesStorage {
    private static final String GET_USER_ID_BY_FILM_ID = "SELECT film_id FROM FILM_LIKES fl WHERE USER_ID IN (SELECT user_id FROM film_likes where film_id in (SELECT film_id FROM FILM_LIKES fl WHERE USER_ID = ?) GROUP BY USER_ID ORDER BY COUNT(USER_ID) DESC) AND NOT film_id IN (SELECT film_id FROM FILM_LIKES fl WHERE USER_ID = ?)";
    private final JdbcTemplate jdbcTemplate;
    private final FilmDBStorage filmDBStorage;

    public LikesDBStorage(JdbcTemplate jdbcTemplate, FilmDBStorage filmDBStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmDBStorage = filmDBStorage;
    }

    @Override
    public List<Film> getFilmsByUserId(int userId) {
        List<Film> films = new ArrayList<>();
        List<Integer> filmsId = jdbcTemplate.query(GET_USER_ID_BY_FILM_ID, likesRowMapper(), userId, userId).stream().findFirst().orElse(new ArrayList<>());
        for (int filmId : filmsId) {
            films.add(filmDBStorage.getFilm(filmId));
        }
        return films;
    }

    private RowMapper<List<Integer>> likesRowMapper() {
        return (rs, rowNum) -> {
            List<Integer> films = new ArrayList<>();
            do {
                films.add(rs.getInt("film_id"));
            } while (rs.next());

            return films;
        };
    }
}
