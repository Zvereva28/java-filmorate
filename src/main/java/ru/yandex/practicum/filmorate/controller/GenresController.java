package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.mappers.FilmorateMapper;
import ru.yandex.practicum.filmorate.model.dto.GenresDTO;
import ru.yandex.practicum.filmorate.service.GenresService;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/genres")
@AllArgsConstructor
public class GenresController {
    private final GenresService genresService;
    private final FilmorateMapper mapper = Mappers.getMapper(FilmorateMapper.class);


    @GetMapping("/{id}")
    public GenresDTO getGenre(@PathVariable int id) {
        return mapper.genresToDto(genresService.getGenre(id));
    }

    @GetMapping
    public List<GenresDTO> getAllGenres() {
        return genresService.getAllGenres()
                .stream()
                .map(mapper::genresToDto)
                .collect(toList());
    }
}
