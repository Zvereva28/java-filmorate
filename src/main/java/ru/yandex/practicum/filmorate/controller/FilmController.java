package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;
import java.util.Optional;

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


   @GetMapping ("/common?userId={userId}&friendId={friendId}")
    public Optional<List<Film>> getSharedMovies(int userId, int friendId ) {
       List<Film> sharedMovies = filmService.getSharedMovies(userId, friendId);

        return Optional.ofNullable(sharedMovies);
    }




    @GetMapping("/popular")
    public List<Film> getPopularFilms(
            @RequestParam(value = "count", defaultValue = "10", required = false) Integer count) {
        return filmService.getPopularFilms(count);
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

}
