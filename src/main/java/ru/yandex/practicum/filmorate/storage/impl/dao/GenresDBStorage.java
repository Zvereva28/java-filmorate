package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.GenresNotFoundException;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.storage.GenresStorage;

import java.util.List;

@Slf4j
@Service
public class GenresDBStorage implements GenresStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenresDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genres getGenresById(int id) {
        List<Genres> genres = jdbcTemplate.query("SELECT  genre_name FROM genre WHERE id = ?",
                (rs, rowNum) -> new Genres(id, rs.getString("genre_name")), id);
        if (genres.size() != 1) {
            throw new GenresNotFoundException("Жанр с id = " + id + " не существует");
        }
        return genres.get(0);
    }

    @Override
    public List<Genres> getAll() {
        List<Genres> genres = jdbcTemplate.query("SELECT id, genre_name FROM genre",
                (rs, rowNum) -> new Genres(rs.getInt("id"), rs.getString("genre_name")));
        return genres;
    }
}
