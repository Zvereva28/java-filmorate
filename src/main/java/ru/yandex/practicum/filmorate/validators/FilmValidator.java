package ru.yandex.practicum.filmorate.validators;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@Slf4j
@Component
public class FilmValidator {
    private static final int MAX_LENGTH_FILM_NAME = 200;
    private static final LocalDate MIN_DATE_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private static final String IS_NO_LIKE = "SELECT COUNT(FILM_ID) FROM FILM_LIKES WHERE USER_ID = ? AND FILM_ID = ?";
    private final JdbcTemplate jdbcTemplate;

    public FilmValidator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void checkFilm(Film film) {

        if (film.getName().isBlank()) {
            log.debug("Название фильма не должно быть пустым ");
            throw new FilmException("Название фильма не должно быть пустым");
        }
        if (film.getDescription().length() > MAX_LENGTH_FILM_NAME) {
            log.debug("Описание фильма должно быть не больше " + MAX_LENGTH_FILM_NAME + " символов");
            throw new FilmException("Описание фильма должно быть не больше " + MAX_LENGTH_FILM_NAME + " символов");
        }
        if (film.getDuration() < 1) {
            log.debug("Продолжительность фильма должна быть больше 0");
            throw new FilmException("Продолжительность фильма должна быть больше 0");
        }
        if (film.getReleaseDate().isBefore(MIN_DATE_RELEASE_DATE)) {
            log.debug("Не верная дата релиза");
            throw new FilmException("Не верная дата релиза");
        }
    }

    public boolean isNoLike(int userId, int filmId) {
        Number row = jdbcTemplate.queryForObject(IS_NO_LIKE, Integer.class, userId, filmId);
        if (row != null && !row.equals(0)) {
            return false;
        } else {
            return true;
        }
    }
}