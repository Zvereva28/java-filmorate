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
@RequestMapping("/films")
public class FilmController {
    private FilmValidator filmValidator = new FilmValidator();
    private Map<Integer, Film> films = new HashMap<>();
    private int idManager = 0;

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film postFilm(@RequestBody Film film) {
        filmValidator.checkFilm(film);
        film.setId(generateId());
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film putFilm(@RequestBody @Validated Film film) {
        if (!films.containsKey(film.getId())) {
            throw new FilmException("Фильма с id = " + film.getId() + " не существует");
        }
        filmValidator.checkFilm(film);
        films.put(film.getId(), film);
        return film;
    }

    private Integer generateId() {
        idManager++;
        return idManager;
    }
}
