package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Component("filmDBStorage")
public class FilmDBStorage implements FilmStorage {
    private static final String SELECT_COUNT_OF_LIKES = "SELECT count(*) AS count FROM film_likes where film_id = ?";
    private static final String UPDATE_FILM = "UPDATE films SET  name=?, description=?, release_date=?, duration=?, rating_mpa=?, count_likes=? WHERE id=?";
    private static final String DELETE_FILM_GENRE = "DELETE FROM film_genre WHERE film_id=?";
    private static final String SELECT_ALL_FILMS = "SELECT f.id, name, description, release_date, duration, rating_mpa, count_likes, fg.genre_id AS genre_id, g.genre_name AS genre_name " +
            "FROM films as f LEFT JOIN film_genre AS fg ON f.id=fg.film_id LEFT JOIN genre AS g ON fg.genre_id=g.id " +
            "ORDER BY f.id, genre_id";
    private static final String SELECT_FILM = "SELECT f.id, name, description, release_date, duration, rating_mpa, count_likes, fg.genre_id AS genre_id, g.genre_name AS genre_name " +
            "FROM films as f LEFT JOIN film_genre AS fg ON f.id=fg.film_id LEFT JOIN genre AS g ON fg.genre_id=g.id " +
            "WHERE f.id =? ORDER BY genre_id";
    private static final String DELETE_LIKES = "DELETE FROM film_likes WHERE film_id=? AND user_id=?";


    private final JdbcTemplate jdbcTemplate;

    public FilmDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film addFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("films")
                .usingGeneratedKeyColumns("id");
        Map<String, String> params = Map.of("name", film.getName(), "description", film.getDescription(),
                "release_date", film.getReleaseDate().toString(),
                "duration", film.getDuration().toString(),
                "rating_mpa", film.getMpa().getId().toString());
        Number id = simpleJdbcInsert.executeAndReturnKey(params);
        simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("film_genre");
        Set<Genres> genres = new HashSet<>(film.getGenres());
        film.getGenres().clear();
        if (genres.size() > 0) {
            for (Genres genre : genres) {
                film.getGenres().add(genre);
                params = Map.of(
                        "genre_id", genre.getId().toString(),
                        "film_id", id.toString());
                simpleJdbcInsert.execute(params);
            }
        }
        film.setId(id.intValue());

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        Integer id = film.getId();
        filmExist(id);
        Integer countLikes;
        try {
            countLikes = jdbcTemplate.queryForObject(SELECT_COUNT_OF_LIKES,
                    (rs, rowNum) -> rs.getInt("count"), film.getId());
        } catch (RuntimeException e) {
            countLikes = 0;
        }
        jdbcTemplate.update(UPDATE_FILM,
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), countLikes, film.getId());

        jdbcTemplate.update(DELETE_FILM_GENRE, id);

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(Objects.requireNonNull(jdbcTemplate.getDataSource()))
                .withTableName("film_genre");
        Set<Genres> genres = new HashSet<>(film.getGenres());
        film.getGenres().clear();
        if (genres.size() > 0) {
            for (Genres genre : genres) {
                Map<String, String> params = Map.of(
                        "genre_id", genre.getId().toString(),
                        "film_id", id.toString());
                simpleJdbcInsert.execute(params);
            }
        }
        return getFilm(id);
    }

    @Override
    public List<Film> getAllFilms() {
        return jdbcTemplate.query(SELECT_ALL_FILMS, filmsRowMapper()).stream().findFirst().orElse(new ArrayList<>());
    }


    @Override
    public Film getFilm(int id) {
        return filmExist(id);
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("film_likes");
        Map<String, String> params = Map.of("user_id", userId.toString(),
                "film_id", filmId.toString());
        simpleJdbcInsert.execute(params);
    }

    @Override
    public void removeLike(Integer filmId, Integer userId) {
        jdbcTemplate.update(DELETE_LIKES,
                filmId, userId);

    }

    private Film filmExist(int id) {
        return jdbcTemplate.query(SELECT_FILM, filmRowMapper(), id).stream()
                .findFirst().orElseThrow(() -> new FilmNotFoundException("Фильм с id = " + id + " не существует"));

    }

    private RowMapper<Film> filmRowMapper() {
        return (rs, rowNum) -> {
            Film film = getColumns(rs);
            if (rs.getInt("genre_id") > 0) {
                do {
                    film.getGenres().add(new Genres(rs.getInt("genre_id"), rs.getString("genre_name")));
                } while (rs.next());
            }
            return film;
        };
    }

    private RowMapper<List<Film>> filmsRowMapper() {
        return (rs, rowNum) -> {
            List<Film> films = new ArrayList<>();
            if (rs.wasNull()) {
                return films;
            }
            Film film = createFilmFromDB(rs);
            while (rs.next()) {
                if (film.getId() != rs.getInt("id")) {
                    films.add(film);
                    film = createFilmFromDB(rs);
                }
            }
            films.add(film);
            return films;
        };
    }

    private Film createFilmFromDB(ResultSet rs) throws SQLException {
        Film film = getColumns(rs);
        if (rs.getInt("genre_id") > 0) {
            film.getGenres().add(new Genres(rs.getInt("genre_id"), rs.getString("genre_name")));
        }
        return film;
    }

    private Film getColumns(ResultSet rs) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));
        film.setMpa(new Mpa(rs.getInt("rating_mpa")));
        film.setCountLikes(rs.getInt("count_likes"));
        return film;
    }
}