package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.dto.FilmDTO;
import ru.yandex.practicum.filmorate.model.mappers.FilmorateMapper;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.ArrayList;
import java.util.List;


import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/films")
@AllArgsConstructor
public class FilmController {
    private final FilmService filmService;
    private final FilmorateMapper mapper = Mappers.getMapper(FilmorateMapper.class);

    @GetMapping
    public List<FilmDTO> getAllFilms() {
            return filmService.getAllFilms()
                    .stream()
                    .map(mapper::filmToDto)
                    .collect(toList());
    }

    @GetMapping("/popular")
    public List<FilmDTO> getPopularFilms(
            @RequestParam(value = "count", defaultValue = "10", required = false) Integer count,
            @RequestParam(value = "genreId", defaultValue = "0", required = false) Integer genreId,
            @RequestParam(value = "year", defaultValue = "0", required = false) Integer year
    ) {
        List<Film> films = filmService.getPopularFilms(count, genreId, year);

        if (films.size() != 0) {
            return films
                    .stream()
                    .map(mapper::filmToDto)
                    .collect(toList());
        } else {
            return new ArrayList<>();
        }
    }

    @GetMapping("/common")
    public List<FilmDTO> getSharedFilms(
            @RequestParam(value = "userId") Integer userId,
            @RequestParam(value = "friendId") Integer friendId) {
        return filmService.getSharedFilms(userId, friendId)
                .stream()
                .map(mapper::filmToDto)
                .collect(toList());
    }

    @GetMapping("/{id}")
    public FilmDTO getFilm(@PathVariable int id) {
        return mapper.filmToDto(filmService.getFilm(id));
    }

    @PostMapping
    public FilmDTO addFilm(@RequestBody @Validated FilmDTO filmDTO) {
        return mapper.filmToDto(filmService.addFilm(mapper.dtoToFilm(filmDTO)));
    }

    @PutMapping
    public FilmDTO updateFilm(@RequestBody @Validated FilmDTO filmDTO) {
        return mapper.filmToDto(filmService.updateFilm(mapper.dtoToFilm(filmDTO)));
    }

    @PutMapping("/{id}/like/{userId}")
    public FilmDTO addLike(@PathVariable int id, @PathVariable int userId) {
        return mapper.filmToDto(filmService.addLike(id, userId));
    }

    @DeleteMapping("/{id}/like/{userId}")
    public FilmDTO deleteLike(@PathVariable int id, @PathVariable int userId) {
        return mapper.filmToDto(filmService.deleteLike(id, userId));
    }

    @GetMapping("/director/{directorId}")
    public List<FilmDTO> getFilmsByYearOrLikes(@PathVariable int directorId, @RequestParam String sortBy) {
        return filmService.getDirectorFilms(directorId, sortBy)
                .stream()
                .map(mapper::filmToDto)
                .collect(toList());
    }

    @GetMapping("/search")
    public List<FilmDTO> searchFilms(@RequestParam("query") String query, @RequestParam("by") List<String> by) {
        return filmService.searchFilms(query, by)
                .stream()
                .map(mapper::filmToDto)
                .collect(toList());
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable int id) {
        filmService.deleteFilm(id);
    }
}
