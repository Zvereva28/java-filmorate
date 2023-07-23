package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@AllArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public List<Film> getFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(
            @RequestParam(value = "count", defaultValue = "10", required = false) Integer count,
            @RequestParam(value = "genreId", defaultValue = "0", required = false) Integer genreId,
            @RequestParam(value = "year", defaultValue = "0", required = false) Integer year
    ) {
        return filmService.getPopularFilms(count, genreId, year);
    }

    @GetMapping("/common")
    public List<Film> getSharedFilms(
            @RequestParam(value = "userId", required = true) Integer userId,
            @RequestParam(value = "friendId", required = true) Integer friendId) {
        return filmService.getSharedFilms(userId, friendId);
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable int id) {
        return filmService.getFilm(id);
    }

    @PostMapping
    public Film postFilm(@RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film putFilm(@RequestBody @Validated Film film) {
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film putLikesFilmById(@PathVariable int id, @PathVariable int userId) {
        return filmService.putLikesFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLikesFilmById(@PathVariable int id, @PathVariable int userId) {
        return filmService.deleteLikesFilm(id, userId);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getFilmsByYearOrLikes(@PathVariable int directorId, @RequestParam String sortBy) {
        return filmService.getDirectorFilms(directorId, sortBy);
    }

    @GetMapping("/search")
    public List<Film> searchFilms(@RequestParam("query") String query, @RequestParam("by") List<String> by) {
        return filmService.searchFilms(query, by);
    }

}
