package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Component("filmDBStorage")
public class FilmDBStorage implements FilmStorage {
    private static final String SELECT_COUNT_OF_LIKES = "SELECT count(*) AS count FROM film_likes where film_id = ?";
    private static final String UPDATE_FILM = "UPDATE films SET  name=?, description=?, release_date=?, duration=?, rating_mpa=?, count_likes=? WHERE id=?";
    private static final String DELETE_FILM_GENRE = "DELETE FROM film_genre WHERE film_id=?";
    private static final String SELECT_ALL_FILMS = "SELECT f.id, name, description, release_date, duration, rating_mpa, count_likes, fg.genre_id AS genre_id, g.genre_name AS genre_name " +
            "FROM films as f LEFT JOIN film_genre AS fg ON f.id=fg.film_id LEFT JOIN genre AS g ON fg.genre_id=g.id " +
            "ORDER BY f.id, genre_id";
    private static final String GET_POPULAR_FILMS = "SELECT f.id, name, description, release_date, duration, rating_mpa, count_likes, fg.genre_id AS genre_id, g.genre_name AS genre_name  " +
            "FROM films as f LEFT JOIN film_genre AS fg ON f.id=fg.film_id LEFT JOIN genre AS g ON fg.genre_id=g.id  " +
            "WHERE f.id IN (%s) " +
            "ORDER BY count_likes DESC, f.id ASC, genre_id ASC";
    private static final String GET_ID_FILMS_WITH_LIMITS = String.format(GET_POPULAR_FILMS, "SELECT id FROM films ORDER BY count_likes DESC LIMIT ? ");

    private static final String GET_ID_FILMS_WITH_GENRES = String.format(GET_POPULAR_FILMS, "SELECT f.id " +
            "FROM films as f LEFT JOIN film_genre AS fg ON f.id=fg.film_id " +
            "WHERE fg.genre_id = ?" +
            " ORDER BY count_likes DESC LIMIT ?");
    private static final String GET_ID_FILMS_WITH_GENRES_YEAR = String.format(GET_POPULAR_FILMS, "SELECT f.id " +
            "FROM films as f LEFT JOIN film_genre AS fg ON f.id=fg.film_id " +
            "WHERE fg.genre_id = ? AND release_date between ? and ? " +
            " ORDER BY count_likes DESC LIMIT ?");
    private static final String GET_ID_FILMS_WITH_YEAR = String.format(GET_POPULAR_FILMS, "SELECT f.id " +
            "FROM films as f LEFT JOIN film_genre AS fg ON f.id=fg.film_id " +
            "WHERE release_date between ? and ? " +
            " ORDER BY count_likes DESC LIMIT ?");
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
    public List<Film> getPopularFilms(int count, int genreId, int year) {
        if (genreId == 0 & year == 0) {
            System.out.println();
            System.out.println(GET_ID_FILMS_WITH_LIMITS);
            System.out.println();
            return jdbcTemplate.query(GET_ID_FILMS_WITH_LIMITS, filmsRowMapper(), count).stream().findFirst().orElse(new ArrayList<>());
        } else if (genreId == 0) {
            System.out.println();
            System.out.println(GET_ID_FILMS_WITH_YEAR);
            System.out.println();
            return jdbcTemplate.query(GET_ID_FILMS_WITH_YEAR, filmsRowMapper(), getStartYear(year), getEndYear(year), count).stream().findFirst().orElse(new ArrayList<>());
        } else if (year == 0) {
            System.out.println();
            System.out.println(GET_ID_FILMS_WITH_GENRES);
            System.out.println();
            return jdbcTemplate.query(GET_ID_FILMS_WITH_GENRES, filmsRowMapper(), genreId, count).stream().findFirst().orElse(new ArrayList<>());
        } else
            System.out.println();
        System.out.println(GET_ID_FILMS_WITH_GENRES_YEAR);
        System.out.println();
        return jdbcTemplate.query(GET_ID_FILMS_WITH_GENRES_YEAR, filmsRowMapper(), genreId, getStartYear(year), getEndYear(year), count).stream().findFirst().orElse(new ArrayList<>());
    }

    private String getStartYear(int year) {
        return year + "-01-01";
    }

    private String getEndYear(int year) {
        return year + "-12-31";
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
            do {
                Film film = createFilmFromDB(rs);
                films.add(film);

            } while (rs.next());
            Map<Integer, Film> filmsHash = new HashMap<>();

            for (Film film1 : films) {
                if (filmsHash.containsKey(film1.getId())) {
                    Film filmWithGenres = filmsHash.get(film1.getId());
                    filmWithGenres.getGenres().add(film1.getGenres().get(0));
                    filmsHash.put(filmWithGenres.getId(), filmWithGenres);
                } else {
                    filmsHash.put(film1.getId(), film1);
                }
            }
            return new ArrayList<>(filmsHash.values());
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