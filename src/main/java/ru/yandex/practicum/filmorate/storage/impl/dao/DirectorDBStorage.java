package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DirectorDBStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    private static final String GET_ALL_DIRECTORS = "SELECT * FROM directors";

    private static final String UPDATE_DIRECTOR = "UPDATE directors SET director_name = ? WHERE director_id = ?";

    private static final String DELETE_DIRECTOR = "DELETE FROM directors WHERE director_id = ?";

    private static final String GET_DIRECTOR = "SELECT * FROM directors WHERE director_id = ?";

    @Override
    public Director addDirector(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("directors")
                .usingGeneratedKeyColumns("director_id");
        Map<String, Object> params = new HashMap<>();
        params.put("director_name", director.getName());
        Number id = simpleJdbcInsert.executeAndReturnKey(params);
        director.setId(id.intValue());

        return director;
    }

    @Override
    public Director getDirector(int id) {
        return directorExist(id);
    }

    @Override
    public List<Director> getAllDirectors() {
        return jdbcTemplate.query(GET_ALL_DIRECTORS, directorRowMapper());
    }

    @Override
    public Director updateDirector(Director director) {
        directorExist(director.getId());
        jdbcTemplate.update(UPDATE_DIRECTOR,
                director.getName(),
                director.getId());
        return director;
    }

    @Override
    public void deleteDirector(int id) {
        directorExist(id);
        jdbcTemplate.update(DELETE_DIRECTOR, id);
    }

    @Override
    public Director directorExist(int id) {
        return jdbcTemplate.query(GET_DIRECTOR, directorRowMapper(), id).stream()
                .findFirst().orElseThrow(() -> new DirectorNotFoundException("Режиссера с id = " + id + " не существует"));

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
