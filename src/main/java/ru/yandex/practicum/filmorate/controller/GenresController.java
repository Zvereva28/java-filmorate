package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.service.GenresService;

import java.util.List;

@RestController
@RequestMapping("/genres")
@AllArgsConstructor
public class GenresController {
    private final GenresService genresService;

    @GetMapping("/{id}")
    public Genres getGenre(@PathVariable int id) {
        return genresService.getGenre(id);
    }

    @GetMapping
    public List<Genres> getAllGenres() {
        return genresService.getAllGenres();
    }
}
