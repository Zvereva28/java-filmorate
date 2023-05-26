package ru.yandex.practicum.filmorate.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FilmException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validators.FilmValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class FilmController {
    private Map<Integer, Film> films = new HashMap<>();
    private int idManager = 0;

    @GetMapping("/films")
    public List<Film> findAllFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping("/films")
    public Film createFilm(@RequestBody Film film) {
        FilmValidator.checkFilm(film);
        film.setId(generateId());
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping("/films")
    public Film updateFilm(@RequestBody @Validated Film film) {
        if (!films.containsKey(film.getId())) {
            throw new FilmException("Фильма с id = " + film.getId() + " не существует");
        }
        FilmValidator.checkFilm(film);
        films.put(film.getId(), film);
        return film;
    }

    private Integer generateId() {
        idManager++;
        return idManager;
    }
}
