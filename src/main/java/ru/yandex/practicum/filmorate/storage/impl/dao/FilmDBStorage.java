package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DbException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
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
@Getter
@Component("filmDBStorage")
public class FilmDBStorage implements FilmStorage {
    private static final String GET_COUNT_OF_LIKES = "SELECT count(*) AS count FROM film_likes where film_id = ?";
    private static final String UPDATE_FILM = "UPDATE films SET  name=?, description=?, release_date=?, duration=?, rating_mpa=?, count_likes=? WHERE id=?";
    private static final String DELETE_FILM_GENRE = "DELETE FROM film_genre WHERE film_id=?";
    private static final String DELETE_FILM_DIRECTOR = "DELETE FROM film_director WHERE film_id=?";
    private static final String GET_ALL_FILMS = "SELECT f.id, name, description, release_date, duration, rating_mpa, count_likes, fg.genre_id AS genre_id, g.genre_name AS genre_name, " +
            "fd.director_id AS director_id, d.director_name AS director_name " +
            "FROM films as f LEFT JOIN film_genre AS fg ON f.id=fg.film_id LEFT JOIN genre AS g ON fg.genre_id=g.id " +
            "LEFT JOIN film_director AS fd ON f.id=fd.film_id " +
            "LEFT JOIN directors AS d ON fd.director_id=d.director_id " +
            "ORDER BY f.id, genre_id";
    private static final String GET_POPULAR_FILMS = "SELECT f.id, name, description, release_date, duration, rating_mpa, count_likes, fg.genre_id AS genre_id, g.genre_name AS genre_name,  " +
            "fd.director_id AS director_id, d.director_name AS director_name " +
            "FROM films as f LEFT JOIN film_genre AS fg ON f.id=fg.film_id LEFT JOIN genre AS g ON fg.genre_id=g.id  " +
            "LEFT JOIN film_director AS fd ON f.id=fd.film_id " +
            "LEFT JOIN directors AS d ON fd.director_id=d.director_id " +
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
    private static final String GET_FILM = "SELECT f.id, name, description, release_date, duration, rating_mpa, count_likes, fg.genre_id AS genre_id, g.genre_name AS genre_name, " +
            "fd.director_id AS director_id, d.director_name AS director_name " +
            "FROM films as f LEFT JOIN film_genre AS fg ON f.id=fg.film_id LEFT JOIN genre AS g ON fg.genre_id=g.id " +
            "LEFT JOIN film_director AS fd ON f.id=fd.film_id LEFT JOIN directors AS d ON fd.director_id=d.director_id " +
            "WHERE f.id =? " +
            "ORDER BY genre_id";


    private static final String DELETE_LIKES = "DELETE FROM film_likes WHERE film_id=? AND user_id=?";
    private static final String DELETE_FILM = "DELETE FROM films WHERE id=?";
    private static final String GET_FILMS_SHARED =
            "SELECT f.id, name, description, release_date, duration, rating_mpa, count_likes, fg.genre_id AS genre_id, g.genre_name AS genre_name, " +
                    "fd.director_id AS director_id, d.director_name AS director_name " +
                    "FROM films as f LEFT JOIN film_genre AS fg ON f.id=fg.film_id LEFT JOIN genre AS g ON fg.genre_id=g.id " +
                    "LEFT JOIN film_director AS fd ON f.id=fd.film_id " +
                    "LEFT JOIN directors AS d ON fd.director_id=d.director_id " +
                    "WHERE f.id IN (SELECT t.film_id FROM (SELECT film_id, COUNT(film_id) AS count FROM public.film_likes " +
                    "WHERE user_id IN (?, ?) " +
                    "GROUP BY film_id) AS t " +
                    "WHERE t.count=2) ORDER BY count_likes DESC, f.id ASC, genre_id ASC";

    private static final String GET_DIRECTOR_FILMS_ORDERBY_YEAR =
            "SELECT f.id, name, description, release_date, duration, rating_mpa, count_likes, " +
                    "fg.genre_id AS genre_id, g.genre_name AS genre_name, " +
                    "fd.director_id AS director_id, d.director_name AS director_name " +
                    "FROM films as f " +
                    "LEFT JOIN film_genre AS fg ON f.id=fg.film_id " +
                    "LEFT JOIN genre AS g ON fg.genre_id=g.id " +
                    "LEFT JOIN film_director AS fd ON f.id=fd.film_id " +
                    "LEFT JOIN directors AS d ON fd.director_id=d.director_id " +
                    "WHERE d.director_id=? " +
                    "ORDER BY release_date";

    private static final String GET_DIRECTOR_FILMS_ORDERBY_LIKES =
            "SELECT f.id, name, description, release_date, duration, rating_mpa, count_likes, " +
                    "fg.genre_id AS genre_id, g.genre_name AS genre_name, " +
                    "fd.director_id AS director_id, d.director_name AS director_name " +
                    "FROM films as f " +
                    "LEFT JOIN film_genre AS fg ON f.id=fg.film_id " +
                    "LEFT JOIN genre AS g ON fg.genre_id=g.id " +
                    "LEFT JOIN film_director AS fd ON f.id=fd.film_id " +
                    "LEFT JOIN directors AS d ON fd.director_id=d.director_id " +
                    "WHERE d.director_id=? " +
                    "ORDER BY count_likes DESC";

    private static final String GET_SEARCH_FILMS =
            "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.rating_mpa, f.count_likes, " +
                    "fg.genre_id AS genre_id, g.genre_name AS genre_name, " +
                    "fd.director_id AS director_id, d.director_name AS director_name " +
                    "FROM films f " +
                    "LEFT JOIN film_genre AS fg ON f.id=fg.film_id " +
                    "LEFT JOIN genre AS g ON fg.genre_id=g.id " +
                    "LEFT JOIN film_director AS fd ON f.id=fd.film_id " +
                    "LEFT JOIN directors AS d ON fd.director_id=d.director_id " +
                    "%s " +
                    "ORDER BY f.count_likes DESC";

    private static final String SEARCH_BY_FILMS = "WHERE LOWER(f.name) LIKE LOWER(?)";
    private static final String SEARCH_BY_DIRECTORS = "WHERE LOWER(d.director_name) LIKE LOWER(?)";
    private static final String SEARCH_BY_FILMS_AND_DIRECTORS = "WHERE LOWER(f.name) LIKE LOWER(?) OR LOWER(d.director_name) LIKE LOWER(?)";

    private final JdbcTemplate jdbcTemplate;

    private DirectorStorage directorStorage;

    public FilmDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    public FilmDBStorage(JdbcTemplate jdbcTemplate, DirectorStorage directorStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.directorStorage = directorStorage;
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
        simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("film_director");
        Set<Director> directors = new HashSet<>(film.getDirectors());
        film.getDirectors().clear();
        if (directors.size() > 0) {
            for (Director director : directors) {
                film.getDirectors().add(director);
                params = Map.of(
                        "director_id", director.getId().toString(),
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
            countLikes = jdbcTemplate.queryForObject(GET_COUNT_OF_LIKES,
                    (rs, rowNum) -> rs.getInt("count"), film.getId());
        } catch (RuntimeException e) {
            countLikes = 0;
        }
        jdbcTemplate.update(UPDATE_FILM,
                film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), countLikes, film.getId());

        jdbcTemplate.update(DELETE_FILM_GENRE, id);
        jdbcTemplate.update(DELETE_FILM_DIRECTOR, id);

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
        simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("film_director");
        Set<Director> directors = new HashSet<>(film.getDirectors());
        film.getDirectors().clear();
        if (directors.size() > 0) {
            for (Director director : directors) {
                film.getDirectors().add(director);
                Map<String, String> params = Map.of(
                        "director_id", director.getId().toString(),
                        "film_id", id.toString());
                simpleJdbcInsert.execute(params);
            }
        }

        return getFilm(id);
    }

    @Override
    public List<Film> getAllFilms() {
        return jdbcTemplate.query(GET_ALL_FILMS, filmsRowMapper()).stream().findFirst().orElse(new ArrayList<>());
    }

    @Override
    public List<Film> getPopularFilms(int count, int genreId, int year) {
        if (genreId == 0 & year == 0) {
            return jdbcTemplate.query(GET_ID_FILMS_WITH_LIMITS, filmsRowMapper(), count).stream().findFirst().orElse(new ArrayList<>());
        } else if (genreId == 0) {
            return jdbcTemplate.query(GET_ID_FILMS_WITH_YEAR, filmsRowMapper(), getStartYear(year), getEndYear(year), count).stream().findFirst().orElse(new ArrayList<>());
        } else if (year == 0) {
            return jdbcTemplate.query(GET_ID_FILMS_WITH_GENRES, filmsRowMapper(), genreId, count).stream().findFirst().orElse(new ArrayList<>());
        } else
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

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(Objects.requireNonNull(jdbcTemplate.getDataSource()))
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

    @Override
    public List<Film> getDirectorFilms(int id, String string) {
        directorStorage.directorExist(id);
        if (string.equals("year")) {
            return jdbcTemplate.query(GET_DIRECTOR_FILMS_ORDERBY_YEAR, filmsRowMapper(), id)
                    .stream().findFirst().orElse(new ArrayList<>());
        }
        if (string.equals("likes")) {
            return jdbcTemplate.query(GET_DIRECTOR_FILMS_ORDERBY_LIKES, filmsRowMapper(), id)
                    .stream().findFirst().orElse(new ArrayList<>());
        }
        return null;
    }

    @Override
    public List<Film> searchFilms(String query, List<String> by) {
        String queryParam = "%" + query + "%";
        if (by.size() == 1 && by.get(0).equals("director")) {
            return jdbcTemplate.query(String.format(GET_SEARCH_FILMS, SEARCH_BY_DIRECTORS),
                    filmsRowMapper(), queryParam).stream().findFirst().orElse(new ArrayList<>());
        } else if (by.size() == 1 && by.get(0).equals("title")) {
            return jdbcTemplate.query(String.format(GET_SEARCH_FILMS, SEARCH_BY_FILMS),
                    filmsRowMapper(), queryParam).stream().findFirst().orElse(new ArrayList<>());
        } else {
            return jdbcTemplate.query(String.format(GET_SEARCH_FILMS, SEARCH_BY_FILMS_AND_DIRECTORS),
                    filmsRowMapper(), queryParam, queryParam).stream().findFirst().orElse(new ArrayList<>());
        }
    }

    private Film filmExist(int id) {
        return jdbcTemplate.query(GET_FILM, filmRowMapper(), id).stream()
                .findFirst().orElseThrow(() -> new FilmNotFoundException("Фильм с id = " + id + " не существует"));

    }

    private RowMapper<Film> filmRowMapper() {
        return (rs, rowNum) -> {
            Film film = getColumns(rs);
            if (rs.getInt("director_id") > 0) {
                film.getDirectors().add(new Director(rs.getInt("director_id"),
                        rs.getString("director_name")));
            }
            if (rs.getInt("genre_id") > 0) {
                do {
                    film.getGenres().add(new Genres(rs.getInt("genre_id"),
                            rs.getString("genre_name")));
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
                if (rs.getInt("genre_id") != 0) {
                    Genres genre = new Genres(rs.getInt("genre_id"), rs.getString("genre_name"));
                    if (!film.getGenres().contains(genre)) {
                        film.getGenres().add(genre);
                    }
                }
            }
            films.add(film);
            return films;
        };

    }

    private Film createFilmFromDB(ResultSet rs) {
        try {
            Film film = getColumns(rs);
            if (rs.getInt("director_id") > 0) {
                film.getDirectors().add(new Director(rs.getInt("director_id"),
                        rs.getString("director_name")));
            }
            if (rs.getInt("genre_id") > 0) {
                film.getGenres().add(new Genres(rs.getInt("genre_id"), rs.getString("genre_name")));
            }
            return film;
        } catch (SQLException e) {
            throw new DbException("Ошибка в БД");
        }
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

    @Override
    public List<Film> getSharedFilms(int userId, int friendId) {
        return jdbcTemplate.query(GET_FILMS_SHARED, filmsRowMapper(), userId, friendId).stream().findFirst().orElse(new ArrayList<>());
    }

    @Override
    public void deleteFilm(int id) {
        jdbcTemplate.update(DELETE_FILM, id);
    }

}