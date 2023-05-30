package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FilmException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validators.FilmValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private FilmValidator filmValidator = new FilmValidator();
    private Map<Integer, Film> films = new HashMap<>();
    private int idManager = 0;

    @GetMapping
    public List<Film> getFilms() {
        var filmsList = new ArrayList<>(films.values());
        log.debug("+ allFilms: {}", filmsList);
        return filmsList;
    }

    @PostMapping
    public Film postFilm(@RequestBody Film film) {
        log.debug("+ createFilm: {}", film);
        filmValidator.checkFilm(film);
        film.setId(generateId());
        films.put(film.getId(), film);
        log.debug("- createFilm: {}", film);
        return film;
    }

    @PutMapping
    public Film putFilm(@RequestBody @Validated Film film) {
        log.debug("+ updateFilm: {}", film);
        if (!films.containsKey(film.getId())) {
            throw new FilmException("Фильма с id = " + film.getId() + " не существует");
        }
        filmValidator.checkFilm(film);
        Film oldFilm = films.get(film.getId());
        oldFilm.setName(film.getName());
        oldFilm.setReleaseDate(film.getReleaseDate());
        oldFilm.setDescription(film.getDescription());
        oldFilm.setDuration(film.getDuration());
        log.debug("- updateFilm: {}", oldFilm);
        return oldFilm;
    }

    private Integer generateId() {
        idManager++;
        return idManager;
    }
}
