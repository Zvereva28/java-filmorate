package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.dto.DirectorDTO;
import ru.yandex.practicum.filmorate.model.mappers.FilmorateMapper;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Getter
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {
    private final DirectorService directorService;
    private final FilmorateMapper mapper = Mappers.getMapper(FilmorateMapper.class);

    @GetMapping()
    public List<DirectorDTO> getAllDirectors() {
        return directorService.getAllDirectors()
                .stream()
                .map(mapper::directorToDto)
                .collect(toList());
    }

    @GetMapping("/{id}")
    public DirectorDTO getDirectorById(@PathVariable int id) {
        return mapper.directorToDto(directorService.getDirectorById(id));
    }

    @PostMapping
    public DirectorDTO addDirector(@Validated @RequestBody DirectorDTO directorDTO) {
        Director director = directorService.addDirector(mapper.dtoToDirector(directorDTO));
        return mapper.directorToDto(director);
    }

    @PutMapping
    public DirectorDTO updateDirector(@Validated @RequestBody DirectorDTO directorDTO) {
        Director director = directorService.updateDirector(mapper.dtoToDirector(directorDTO));
        return mapper.directorToDto(director);
    }

    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable int id) {
        directorService.deleteDirector(id);
    }
}
