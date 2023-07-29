package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.derectorExceptions.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.exception.filmExceptions.FilmException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class DirectorDBStorage implements DirectorStorage {

    private static final String GET_ALL_DIRECTORS = "SELECT director_id, director_name FROM directors";
    private static final String UPDATE_DIRECTOR = "UPDATE directors SET director_name = ? WHERE director_id = ?";
    private static final String DELETE_DIRECTOR = "DELETE FROM directors WHERE director_id = ?";
    private static final String GET_DIRECTOR = "SELECT director_id, director_name FROM directors WHERE director_id = ?";
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Director addDirector(Director director) {
        try {
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(Objects.requireNonNull(jdbcTemplate.getDataSource()))
                    .withTableName("directors")
                    .usingGeneratedKeyColumns("director_id");
            Map<String, Object> params = new HashMap<>();
            params.put("director_name", director.getName());
            Number id = simpleJdbcInsert.executeAndReturnKey(params);
            director.setId(id.intValue());
        } catch (NullPointerException e) {
            throw new FilmException("Режиссер " + director.getName() + "не создан");
        }

        return director;
    }

    @Override
    public Director getDirector(int id) {

        return jdbcTemplate.query(GET_DIRECTOR, directorRowMapper(), id).stream()
                .findFirst().orElseThrow(() -> new DirectorNotFoundException("Режиссера с id = " + id + " не существует"));
    }

    @Override
    public List<Director> getAllDirectors() {
        return jdbcTemplate.query(GET_ALL_DIRECTORS, directorRowMapper());
    }

    @Override
    public Director updateDirector(Director director) {
        int change = jdbcTemplate.update(UPDATE_DIRECTOR,
                director.getName(),
                director.getId());
        if (change == 0) {
            throw new DirectorNotFoundException("Режиссер с id = " + director.getId() + " не существует");
        }

        return director;
    }

    @Override
    public void deleteDirector(int id) {
        int change = jdbcTemplate.update(DELETE_DIRECTOR, id);
        if (change == 0) {
            throw new DirectorNotFoundException("Режиссер с id = " + id + " не существует");
        }
    }

    private RowMapper<Director> directorRowMapper() {
        return (rs, rowNum) -> {
            Director director = new Director();
            director.setId(rs.getInt("director_id"));
            director.setName(rs.getString("director_name"));

            return director;
        };
    }
}
