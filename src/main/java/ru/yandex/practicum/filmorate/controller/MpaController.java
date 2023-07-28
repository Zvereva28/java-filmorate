package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.dto.MpaDTO;
import ru.yandex.practicum.filmorate.model.mappers.FilmorateMapper;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/mpa")
@AllArgsConstructor
public class MpaController {
    private final MpaService mpaService;
    private final FilmorateMapper mapper = Mappers.getMapper(FilmorateMapper.class);


    @GetMapping("/{id}")
    public MpaDTO getMpa(@PathVariable int id) {
        return mapper.mpaToDto(mpaService.getMpa(id));
    }

    @GetMapping
    public List<MpaDTO> getAllMpa() {
        return mpaService.getAllMpa()
                .stream()
                .map(mapper::mpaToDto)
                .collect(toList());
    }
}
