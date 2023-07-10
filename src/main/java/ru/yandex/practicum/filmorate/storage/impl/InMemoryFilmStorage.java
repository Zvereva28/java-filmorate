package ru.yandex.practicum.filmorate.storage.impl;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
@NoArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int idManager = 0;

    @Override
    public Film addFilm(Film film) {
        film.setId(generateId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new FilmNotFoundException("Фильма с id = " + film.getId() + " не существует");
        }
        Film oldFilm = films.get(film.getId());
        oldFilm.setName(film.getName());
        oldFilm.setReleaseDate(film.getReleaseDate());
        oldFilm.setDescription(film.getDescription());
        oldFilm.setDuration(film.getDuration());
        return oldFilm;
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilm(int id) {
        if (!films.containsKey(id)) {
            throw new FilmNotFoundException("Фильма с id = " + id + " не существует");
        }
        return films.get(id);
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {

    }

    @Override
    public void removeLike(Integer filmId, Integer userId) {

    }

    private Integer generateId() {
        idManager++;
        return idManager;
    }
}